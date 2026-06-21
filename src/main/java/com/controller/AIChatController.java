package com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.model.ChatRequest;
import com.model.ChatResponse;
import com.model.ErrorResponse;
import com.service.AIChatService;

import exceptions.InvalidInputException;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class AIChatController {

	private final AIChatService chatService;

	// Constructor Injection
	public AIChatController(AIChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/message")
	public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {

		ChatResponse response = chatService.processMessage(request);

		return ResponseEntity.ok(response);
	}

	// Health check endpoint
	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Bot is running! ✅");
	}

}
