package com.example.configtracker.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChange {
  private Long id;
  @NotNull(message = "RuleTypeId must not be null")
  private Long ruleTypeId;
  @NotBlank(message = "current value field should not be blank")
  private String currentValue;
  @NotNull (message = "changedBy field should not be blank")
  private String changedBy;
  private LocalDateTime changedAt;
  @NotNull(message = "is critical field should not be empty")
  private boolean isCritical;

  private ConfigChange prevChange; // on id
}
