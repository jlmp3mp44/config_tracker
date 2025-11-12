package com.example.configtracker.repo;

import com.example.configtracker.entities.RuleType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RuleTypeRepo {

  private final Map<Long, RuleType> storage = new HashMap<>();

  public Long generateId() {
    return storage.keySet().stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0L) + 1;
  }

  public List<RuleType> findAll() {
    return storage.values().stream().toList();
  }

  public Optional<RuleType> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public RuleType save(RuleType ruleType) {
    if (ruleType.getId() == null) {
      ruleType.setId(generateId());
    }
    storage.put(ruleType.getId(), ruleType);
    return ruleType;
  }

  public void deleteById(Long id) {
    storage.remove(id);
  }

  public Optional<RuleType> findByName(String name) {
    return storage.values().stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
  }

  public void clear() {
    storage.clear();
  }
}

