package com.example.configtracker.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeResponceDTO {
  private Long id;
  private String currentValue;
  private String changedBy;
  private LocalDateTime changedAt;
  private boolean critical;
}
