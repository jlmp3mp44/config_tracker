package com.example.configtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeUpdateRequest {
  @NotBlank(message = "current value field should not be blank")
  private String currentValue;

  @NotNull(message = "changedBy field should not be blank")
  private String changedBy;

  @NotNull(message = "is critical field should not be empty")
  private boolean isCritical;
}

