package com.example.configtracker.repo;

import com.example.configtracker.entities.ConfigChange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigChangeRepo {

  private final Map<Long, ConfigChange> storage = new HashMap<>();
  private Long counter = 1L;

  public Long generateId() {
    return storage.keySet().stream()
        .mapToLong(Long::longValue)
        .max()
        .orElse(0L) + 1;
  }


  public List<ConfigChange> findAll() {
    return storage.values().stream().toList();
  }

  public Optional<ConfigChange> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public ConfigChange save(ConfigChange change) {
    if (change.getId() == null) {
      change.setId(generateId());
    }
    storage.put(change.getId(), change);
    return change;
  }

  public void deleteById(Long id) {
    storage.remove(id);
  }


}
