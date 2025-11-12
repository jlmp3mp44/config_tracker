package com.example.configtracker.entities;

public enum ValueType {
  INTEGER,
  BOOLEAN,
  STRING;

  public static boolean isValid(String value) {
    if (value == null) return false;
    for (ValueType vt : values()) {
      if (vt.name().equalsIgnoreCase(value)) {
        return true;
      }
    }
    return false;
  }
}
