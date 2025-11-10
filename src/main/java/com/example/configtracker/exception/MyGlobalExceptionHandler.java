package com.example.configtracker.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    Map<String, String> response = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(err -> {
      String fieldName = ((FieldError) err).getField();
      String message = err.getDefaultMessage();
      response.put(fieldName, message);
    });
    return new ResponseEntity<>(response,
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> myResourceNotFoundException(ResourceNotFoundException e) {
    String responce = e.getMessage();
    return new ResponseEntity<String>(responce, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(APIException.class)
  public ResponseEntity<String> myAPIException(APIException e) {
    String message = e.getMessage();
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
    Map<String, String> response = new HashMap<>();
    response.put("error", "Malformed JSON or invalid field type. Please check your input.");
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
    Map<String, String> response = new HashMap<>();
    response.put("error", e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidValueTypeException.class)
  public ResponseEntity<Map<String, String>> handleInvalidValueType(InvalidValueTypeException e) {
    Map<String, String> response = new HashMap<>();
    response.put("error", e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}


