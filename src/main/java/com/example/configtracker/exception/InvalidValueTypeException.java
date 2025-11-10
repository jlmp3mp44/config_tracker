package com.example.configtracker.exception;

public class InvalidValueTypeException extends RuntimeException {
  public InvalidValueTypeException(String value, String allowed) {
    super("Invalid valueType '" + value + "'. Allowed values: " + allowed);
  }
}
