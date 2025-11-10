package com.example.configtracker.repo;

import com.example.configtracker.entities.RuleType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RuleTypeRepo {

  private final List<RuleType> ruleTypes = new ArrayList<>();
  private Long counter = 1L;

  public Long generateId() {
    return counter++;
  }

  public List<RuleType> findAll() {
    return new ArrayList<>(ruleTypes);
  }

  public Optional<RuleType> findById(Long id) {
    return ruleTypes.stream().filter(r -> r.getId().equals(id)).findFirst();
  }

  public RuleType save(RuleType ruleType) {
    if (ruleType.getId() == null) {
      ruleType.setId(generateId());
    }
    ruleTypes.removeIf(r -> r.getId().equals(ruleType.getId())); // оновлення, якщо існує
    ruleTypes.add(ruleType);
    return ruleType;
  }

  public void deleteById(Long id) {
    ruleTypes.removeIf(r -> r.getId().equals(id));
  }

  public Optional<RuleType> findByName(String name) {
    return ruleTypes.stream()
        .filter(r -> r.getName().equalsIgnoreCase(name))
        .findFirst();
  }
}

