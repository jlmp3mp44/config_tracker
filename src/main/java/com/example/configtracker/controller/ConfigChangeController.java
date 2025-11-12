package com.example.configtracker.controller;

import com.example.configtracker.dto.ConfigChangeListDTO;
import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.service.ConfigChangeService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config-changes")
public class ConfigChangeController {

  @Autowired
  private ConfigChangeService service;

  private final Logger log = LoggerFactory.getLogger(ConfigChangeController.class);

  @PostMapping
  public ResponseEntity<ConfigChange> createChange(@Valid @RequestBody ConfigChange change) {
    log.info("Received request to create config change for ruleId={}", change.getRuleTypeId());
    return ResponseEntity.ok(service.logChange(change));
  }

  @GetMapping
  public ResponseEntity<List<ConfigChangeListDTO>> getChanges(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) LocalDateTime from,
      @RequestParam(required = false) LocalDateTime to) {
    log.info("Received request to get all config changes");
    return ResponseEntity.ok(service.listChanges(Optional.ofNullable(type), Optional.ofNullable(from), Optional.ofNullable(to)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ConfigChange> getChangeById(@PathVariable Long id) {
    log.info("Received request to get config change for changeId={}",id);
    return ResponseEntity.ok(service.getChangeById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ConfigChange> delete(@PathVariable Long id) {
    log.info("Received request to delete config change for changeId={}", id);
    ConfigChange configChangeDeleted = service.delete(id);
    return ResponseEntity.ok(configChangeDeleted);
  }
}

