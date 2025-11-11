package com.example.configtracker.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleType {
  private Long id;
  @NotNull
  @Size(min = 3, max = 100, message = "rule name should contains between 3 and 100 characters")
  @Pattern(regexp = "^(?=.*[A-Za-z]).*$", message = "Name must contains English letters")
  private String name;
  private String valueType;
}
