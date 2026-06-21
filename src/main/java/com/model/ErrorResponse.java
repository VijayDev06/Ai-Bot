package com.model;

public class ErrorResponse {
	
	private String message;
	
    private String errorCode;
    
    private long timestamp;

    public ErrorResponse() {}

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

}
