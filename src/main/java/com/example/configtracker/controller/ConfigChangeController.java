package com.example.configtracker.controller;

import com.example.configtracker.dto.ConfigChangeListDTO;
import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.service.ConfigChangeService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @PostMapping
  public ResponseEntity<ConfigChange> createChange(@Valid @RequestBody ConfigChange change) {
    return ResponseEntity.ok(service.logChange(change));
  }

  @GetMapping
  public ResponseEntity<List<ConfigChangeListDTO>> getChanges(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) LocalDateTime from,
      @RequestParam(required = false) LocalDateTime to) {
    return ResponseEntity.ok(service.listChanges(Optional.ofNullable(type), Optional.ofNullable(from), Optional.ofNullable(to)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ConfigChange> getChangeById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getChangeById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ConfigChange> delete(@PathVariable Long id) {
    ConfigChange configChangeDeleted = service.delete(id);
    return ResponseEntity.ok(configChangeDeleted);
  }
}

