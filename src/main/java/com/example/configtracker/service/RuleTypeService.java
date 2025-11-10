package com.example.configtracker.service;

import com.example.configtracker.entities.RuleType;
import java.util.List;

public interface RuleTypeService {

  RuleType createRuleType(RuleType ruleType);

  List<RuleType> listRuleTypes();

  RuleType getRuleTypeById(Long id);

  RuleType updateRuleType(Long id, RuleType updatedRuleType);

  RuleType deleteRuleType(Long id);
}
