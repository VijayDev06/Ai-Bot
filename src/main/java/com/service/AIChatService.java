package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.model.ChatRequest;
import com.model.ChatResponse;

import exceptions.AIServiceException;
import exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AIChatService {

	@Value("${openai.api.key}")
	private String apiKey;

	@Value("${openai.api.url}")
	private String apiUrl;

	@Value("${openai.model}")
	private String model;

	@Value("${openai.max.tokens}")
	private int maxTokens;

	@Value("${openai.temperature}")
	private double temperature;

	private final RestTemplate restTemplate;

	public AIChatService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ChatResponse processMessage(ChatRequest request) {

		validateRequest(request);

		String userMessage = request.getMessage();

		List<Map<String, String>> history = request.getHistory() != null ? request.getHistory()
				: new ArrayList<Map<String, String>>();

		String aiReply = callOpenAI(userMessage, history);

		return new ChatResponse(aiReply, System.currentTimeMillis());
	}

	private void validateRequest(ChatRequest request) {

		if (request == null) {
			throw new InvalidInputException("Request cannot be null");
		}

		if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {

			throw new InvalidInputException("Message cannot be empty");
		}
	}

	private String callOpenAI(String userMessage, List<Map<String, String>> history) {

		try {

			List<Map<String, String>> messages = buildMessages(userMessage, history);

			Map<String, Object> requestBody = buildRequestBody(messages);

			ResponseEntity<Map> response = sendToOpenAI(requestBody);

			return parseOpenAIResponse(response);

		} catch (RestClientException e) {

			log.error("Error while calling OpenAI API", e);

			throw new AIServiceException("OpenAI service is currently unavailable");

		} catch (Exception e) {

			log.error("Unexpected error", e);

			throw new AIServiceException("Unexpected error while processing request");
		}
	}

	/**
	 * Build chat messages
	 */
	private List<Map<String, String>> buildMessages(String userMessage, List<Map<String, String>> history) {

		List<Map<String, String>> messages = new ArrayList<Map<String, String>>();

		Map<String, String> systemMessage = new HashMap<String, String>();

		systemMessage.put("role", "system");
		systemMessage.put("content",
				"You are a helpful, professional AI assistant. " + "Provide clear, accurate and concise answers.");

		messages.add(systemMessage);

		if (history != null && !history.isEmpty()) {

			int start = Math.max(0, history.size() - 5);

			messages.addAll(history.subList(start, history.size()));
		}

		Map<String, String> userMsg = new HashMap<String, String>();

		userMsg.put("role", "user");
		userMsg.put("content", userMessage);

		messages.add(userMsg);

		return messages;
	}

	/**
	 * Build OpenAI request body
	 */
	private Map<String, Object> buildRequestBody(List<Map<String, String>> messages) {

		Map<String, Object> requestBody = new HashMap<String, Object>();

		requestBody.put("model", model);
		requestBody.put("messages", messages);
		requestBody.put("max_tokens", maxTokens);
		requestBody.put("temperature", temperature);

		return requestBody;
	}

	/**
	 * Send request to OpenAI
	 */
	private ResponseEntity<Map> sendToOpenAI(Map<String, Object> requestBody) {

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(requestBody, headers);

		return restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
	}

	/**
	 * Parse OpenAI response
	 */
	@SuppressWarnings("unchecked")
	private String parseOpenAIResponse(ResponseEntity<Map> response) {

		if (response == null || response.getBody() == null) {

			throw new AIServiceException("Empty response received from OpenAI");
		}

		Map<String, Object> responseBody = response.getBody();

		List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

		if (choices == null || choices.isEmpty()) {

			throw new AIServiceException("No response choices received from OpenAI");
		}

		Map<String, Object> choice = choices.get(0);

		Map<String, String> message = (Map<String, String>) choice.get("message");

		if (message == null || message.get("content") == null) {

			throw new AIServiceException("Invalid response content from OpenAI");
		}

		return message.get("content").trim();
	}

}
