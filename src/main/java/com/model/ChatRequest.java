package com.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ChatRequest {

	private String message;
	
	private List<Map<String, String>> history;
	
	public ChatRequest() {}

    public ChatRequest(String message, List<Map<String, String>> history) {
        this.message = message;
        this.history = history;
    }
    
}
