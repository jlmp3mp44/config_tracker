package com.example.configtracker.controller;

import com.example.configtracker.entities.RuleType;
import com.example.configtracker.service.RuleTypeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rule-types")
public class RuleTypeController {

  @Autowired
  private RuleTypeService service;

  @PostMapping
  public ResponseEntity<RuleType> createRuleType(@Valid @RequestBody RuleType ruleType) {
    return ResponseEntity.ok(service.createRuleType(ruleType));
  }

  @GetMapping
  public ResponseEntity<List<RuleType>> getAllRuleTypes() {
    return ResponseEntity.ok(service.listRuleTypes());
  }

  @GetMapping("/{id}")
  public ResponseEntity<RuleType> getRuleTypeById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getRuleTypeById(id));
  }

  @PutMapping("/{ruleId}")
  public ResponseEntity<RuleType> updateRuleType(
      @PathVariable Long ruleId,
      @Valid @RequestBody RuleType updatedRuleType
  ) {
    return ResponseEntity.ok(service.updateRuleType(ruleId, updatedRuleType));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<RuleType> deleteRuleType(@PathVariable Long id) {
    return ResponseEntity.ok(service.deleteRuleType(id));
  }
}

