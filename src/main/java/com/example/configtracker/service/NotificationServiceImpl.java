package com.example.configtracker.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  private static final String DIRECTORY_PATH = "logs";
  private static final String FILE_PATH = DIRECTORY_PATH + "/notifications.log";

  @Override
  public void notify(Object change) {
    try {
      File directory = new File(DIRECTORY_PATH);
      if (!directory.exists()) {
        directory.mkdirs();
      }

      try (FileWriter fw = new FileWriter(FILE_PATH, true);
          PrintWriter pw = new PrintWriter(fw)) {

        pw.println(LocalDateTime.now() + " - " + change.toString());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
