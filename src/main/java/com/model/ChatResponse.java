package com.model;

import lombok.Data;

@Data
public class ChatResponse {
	
	private String reply;
	
    private long timestamp;
    
    public ChatResponse() {}

    public ChatResponse(String reply, long timestamp) {
        this.reply = reply;
        this.timestamp = timestamp;
    }

}
