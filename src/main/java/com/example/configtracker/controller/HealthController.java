package com.example.configtracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

  private static final String LOG_FILE_PATH = "logs/notifications.log"; // шлях до твого лог-файлу

  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> status = new HashMap<>();

    status.put("status", "UP");
    status.put("timestamp", LocalDateTime.now().toString());
    status.put("service", "Config Change Tracker");

    File logFile = new File(LOG_FILE_PATH);
    if (logFile.exists() && logFile.canWrite()) {
      status.put("logFile", "AVAILABLE");
      status.put("logFileSize", logFile.length() + " bytes");
    } else {
      status.put("logFile", "MISSING_OR_UNWRITABLE");
    }

    return ResponseEntity.ok(status);
  }
}

