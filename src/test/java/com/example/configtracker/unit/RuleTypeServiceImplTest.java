package com.example.configtracker.unit;

import com.example.configtracker.entities.RuleType;
import com.example.configtracker.entities.ValueType;
import com.example.configtracker.exception.APIException;
import com.example.configtracker.exception.InvalidValueTypeException;
import com.example.configtracker.exception.ResourceNotFoundException;
import com.example.configtracker.repo.RuleTypeRepo;
import com.example.configtracker.service.RuleTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleTypeServiceImplTest {

  @Mock
  private RuleTypeRepo ruleTypeRepo;

  @InjectMocks
  private RuleTypeServiceImpl ruleTypeService;

  private RuleType sampleRule;

  @BeforeEach
  void setUp() {
    sampleRule = new RuleType();
    sampleRule.setId(1L);
    sampleRule.setName("MaxConnections");
    sampleRule.setValueType("INTEGER");
  }

  @Test
  void createRuleType_shouldSave_whenValid() {
    when(ruleTypeRepo.findByName("MaxConnections")).thenReturn(Optional.empty());
    when(ruleTypeRepo.save(any(RuleType.class))).thenReturn(sampleRule);

    RuleType result = ruleTypeService.createRuleType(sampleRule);

    assertEquals("MaxConnections", result.getName());
    assertEquals("INTEGER", result.getValueType());
    verify(ruleTypeRepo).save(sampleRule);
  }

  @Test
  void createRuleType_shouldThrowAPIException_whenNameExists() {
    when(ruleTypeRepo.findByName("MaxConnections")).thenReturn(Optional.of(sampleRule));

    APIException ex = assertThrows(APIException.class, () ->
        ruleTypeService.createRuleType(sampleRule));

    assertTrue(ex.getMessage().contains("already exists"));
    verify(ruleTypeRepo, never()).save(any());
  }

  @Test
  void createRuleType_shouldThrowInvalidValueTypeException_whenInvalidValueType() {
    sampleRule.setValueType("INVALID");

    assertThrows(InvalidValueTypeException.class, () ->
        ruleTypeService.createRuleType(sampleRule));
    verify(ruleTypeRepo, never()).save(any());
  }

  @Test
  void listRuleTypes_shouldReturnList_whenExists() {
    when(ruleTypeRepo.findAll()).thenReturn(List.of(sampleRule));

    List<RuleType> result = ruleTypeService.listRuleTypes();

    assertEquals(1, result.size());
    assertEquals("MaxConnections", result.get(0).getName());
  }

  @Test
  void listRuleTypes_shouldThrowAPIException_whenEmpty() {
    when(ruleTypeRepo.findAll()).thenReturn(Collections.emptyList());

    assertThrows(APIException.class, () -> ruleTypeService.listRuleTypes());
  }

  @Test
  void getRuleTypeById_shouldReturn_whenFound() {
    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.of(sampleRule));

    RuleType result = ruleTypeService.getRuleTypeById(1L);

    assertEquals(sampleRule, result);
  }

  @Test
  void getRuleTypeById_shouldThrowResourceNotFound_whenNotFound() {
    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> ruleTypeService.getRuleTypeById(1L));
  }

  @Test
  void updateRuleType_shouldUpdate_whenValid() {
    RuleType updated = new RuleType();
    updated.setName("ConnectionLimit");
    updated.setValueType("INTEGER");

    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.of(sampleRule));
    when(ruleTypeRepo.findByName("ConnectionLimit")).thenReturn(Optional.empty());
    when(ruleTypeRepo.save(any(RuleType.class))).thenReturn(updated);

    RuleType result = ruleTypeService.updateRuleType(1L, updated);

    assertEquals("ConnectionLimit", result.getName());
    assertEquals("INTEGER", result.getValueType());
  }

  @Test
  void updateRuleType_shouldThrowAPIException_whenNameAlreadyExists() {
    RuleType updated = new RuleType();
    updated.setName("ExistingRule");
    updated.setValueType("INTEGER");

    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.of(sampleRule));
    when(ruleTypeRepo.findByName("ExistingRule")).thenReturn(Optional.of(new RuleType()));

    assertThrows(APIException.class, () -> ruleTypeService.updateRuleType(1L, updated));
  }

  @Test
  void updateRuleType_shouldThrowInvalidValueTypeException_whenInvalidValueType() {
    RuleType updated = new RuleType();
    updated.setName("NewName");
    updated.setValueType("WRONG");

    assertThrows(InvalidValueTypeException.class, () ->
        ruleTypeService.updateRuleType(1L, updated));
  }


  @Test
  void deleteRuleType_shouldDelete_whenExists() {
    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.of(sampleRule));

    RuleType result = ruleTypeService.deleteRuleType(1L);

    assertEquals(sampleRule, result);
    verify(ruleTypeRepo).deleteById(1L);
  }

  @Test
  void deleteRuleType_shouldThrowResourceNotFound_whenNotFound() {
    when(ruleTypeRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> ruleTypeService.deleteRuleType(1L));
    verify(ruleTypeRepo, never()).deleteById(any());
  }
}
