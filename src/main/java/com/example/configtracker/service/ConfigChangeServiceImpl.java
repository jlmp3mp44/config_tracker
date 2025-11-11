package com.example.configtracker.service;

import com.example.configtracker.dto.ConfigChangeListDTO;
import com.example.configtracker.dto.ConfigChangeResponceDTO;
import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.entities.RuleType;
import com.example.configtracker.exception.APIException;
import com.example.configtracker.exception.ResourceNotFoundException;
import com.example.configtracker.repo.ConfigChangeRepo;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigChangeServiceImpl implements ConfigChangeService {

  @Autowired
  private ConfigChangeRepo configChangeRepo;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private RuleTypeService ruleTypeService;

  @Override
  public ConfigChange logChange(ConfigChange change) {

    RuleType ruleType = ruleTypeService.getRuleTypeById(change.getRuleTypeId());

    boolean duplicateExists = configChangeRepo.findAll().stream()
        .anyMatch(existing ->
            existing.getRuleTypeId().equals(change.getRuleTypeId()) &&
                existing.getCurrentValue().equals(change.getCurrentValue()) &&
                existing.isCritical() == change.isCritical()
        );

    if (duplicateExists) {
      throw new APIException("An identical configuration change already exists. Duplicate not allowed.");
    }

    // Перевіряємо значення
    validateValueType(change, ruleType);

    // Set timestamp and ID
    change.setChangedAt(LocalDateTime.now());
    change.setId(configChangeRepo.generateId()); // generateId() is a simple counter in repo

    // Save the change

    configChangeRepo.save(change);

    // Notify if it's critical
    if (change.isCritical()) {
      notificationService.notify("Critical configuration change detected: " + change);
    }

    return change;
  }

  /**
   * Get all changes with optional filters.
   */
  @Override
  public List<ConfigChangeListDTO> listChanges(Optional<String> typeName, Optional<LocalDateTime> from,
      Optional<LocalDateTime> to) {
    List<ConfigChange> changes = configChangeRepo.findAll().stream()
        .filter(c -> typeName.map(name ->
            ruleTypeService.getRuleTypeById(c.getRuleTypeId()).getName().equalsIgnoreCase(name)
        ).orElse(true))
        .filter(c -> from.map(f -> c.getChangedAt().isAfter(f)).orElse(true))
        .filter(c -> to.map(t -> c.getChangedAt().isBefore(t)).orElse(true))
        .toList();

    if (changes.isEmpty()) {
      throw new APIException("No configuration changes found");
    }
    return toHistoryDTO(changes);
  }


  /**
   * Get a change by its ID.
   */
  @Override
  public ConfigChange getChangeById(Long id) {
    return configChangeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Config change", "configChangeId", id));
  }

  @Override
  public ConfigChange delete(Long id) {
    ConfigChange configChange = configChangeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Config change", "configChangeId", id));
    configChangeRepo.deleteById(id);
    return configChange;
  }

  private void validateValueType(ConfigChange change, RuleType ruleType) {
    String type = ruleType.getValueType();
    String value = change.getCurrentValue();

    switch (type) {
      case "INTEGER":
        try {
          Integer.parseInt(value);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Value for rule " + ruleType.getName() + " must be an integer");
        }
        break;
      case "BOOLEAN":
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
          throw new IllegalArgumentException(
              "Value for rule " + ruleType.getName() + " must be boolean");
        }
        break;
      case "STRING":
        // можна додати додаткові обмеження, наприклад максимальна довжина
        break;

      default:
        throw new IllegalArgumentException(
            "Unknown value type for rule " + ruleType.getName());
    }
  }

  private List<ConfigChangeListDTO> toHistoryDTO(List<ConfigChange> changes) {
    return changes.stream()
        .collect(Collectors.groupingBy(ConfigChange::getRuleTypeId))
        .entrySet()
        .stream()
        .map(entry -> {
          Long ruleTypeId = entry.getKey();
          String ruleName = ruleTypeService.getRuleTypeById(ruleTypeId).getName();

          List<ConfigChangeResponceDTO> history = entry.getValue().stream()
              .sorted(Comparator.comparing(ConfigChange::getChangedAt))
              .map(c -> new ConfigChangeResponceDTO(
                  c.getId(),
                  c.getCurrentValue(),
                  c.getChangedBy(),
                  c.getChangedAt(),
                  c.isCritical()
              ))
              .toList();

          return new ConfigChangeListDTO(ruleTypeId, ruleName, history);
        })
        .toList();
  }

}
