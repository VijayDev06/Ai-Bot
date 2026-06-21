package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.model.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {

		ErrorResponse error = new ErrorResponse(ex.getMessage(), "INVALID_INPUT");

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AIServiceException.class)
	public ResponseEntity<ErrorResponse> handleAIServiceException(AIServiceException ex) {

		ErrorResponse error = new ErrorResponse("AI service unavailable: " + ex.getMessage(), "SERVICE_ERROR");

		return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {

		log.error("Unexpected Error: " + ex.getMessage());

		ErrorResponse error = new ErrorResponse("Something went wrong. Please try again.", "SERVER_ERROR");

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
