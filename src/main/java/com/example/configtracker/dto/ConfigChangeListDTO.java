package com.example.configtracker.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeListDTO {
  private Long ruleTypeId;
  private String ruleName;
  private List<ConfigChangeResponceDTO> history;
}