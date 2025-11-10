package com.example.configtracker.service;

import com.example.configtracker.entities.RuleType;
import com.example.configtracker.entities.ValueType;
import com.example.configtracker.exception.APIException;
import com.example.configtracker.exception.InvalidValueTypeException;
import com.example.configtracker.exception.ResourceNotFoundException;
import com.example.configtracker.repo.RuleTypeRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleTypeServiceImpl implements RuleTypeService {

  @Autowired
  private RuleTypeRepo ruleTypeRepo;

  @Override
  public RuleType createRuleType(RuleType ruleType) {
    validateValueType(ruleType);
    Optional<RuleType> existing = ruleTypeRepo.findByName(ruleType.getName());
    if (existing.isPresent()) {
      throw new APIException("Rule with name '" + ruleType.getName() + "' already exists!");
    }
    return ruleTypeRepo.save(ruleType);
  }

  @Override
  public List<RuleType> listRuleTypes() {
    List<RuleType> ruleTypes = ruleTypeRepo.findAll();
    if (ruleTypes.isEmpty()) {
      throw new APIException("No rule types created yet!");
    }
    return ruleTypes;
  }

  @Override
  public RuleType getRuleTypeById(Long id) {
    return ruleTypeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("RuleType", "id", id));
  }

  @Override
  public RuleType updateRuleType(Long id, RuleType updatedRuleType) {
    validateValueType(updatedRuleType);
    RuleType existing = ruleTypeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("RuleType", "id", id));

    // якщо змінюється ім'я — перевіряємо на дублікати
    if (!existing.getName().equalsIgnoreCase(updatedRuleType.getName())) {
      ruleTypeRepo.findByName(updatedRuleType.getName()).ifPresent(r -> {
        throw new APIException("Rule with name '" + updatedRuleType.getName() + "' already exists!");
      });
    }

    existing.setName(updatedRuleType.getName());
    existing.setValueType(updatedRuleType.getValueType());

    return ruleTypeRepo.save(existing);
  }

  @Override
  public RuleType deleteRuleType(Long id) {
    RuleType existing = ruleTypeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("RuleType", "id", id));
    ruleTypeRepo.deleteById(id);
    return existing;
  }

  private void validateValueType(RuleType ruleType) {
    if (!ValueType.isValid(ruleType.getValueType())) {
      throw new InvalidValueTypeException(
          ruleType.getValueType(),
          java.util.Arrays.toString(ValueType.values())
      );
    }
  }

}

