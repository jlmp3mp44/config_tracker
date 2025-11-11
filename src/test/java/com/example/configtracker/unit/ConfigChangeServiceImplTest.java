package com.example.configtracker.unit;

import com.example.configtracker.dto.ConfigChangeListDTO;
import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.entities.RuleType;
import com.example.configtracker.exception.APIException;
import com.example.configtracker.exception.ResourceNotFoundException;
import com.example.configtracker.repo.ConfigChangeRepo;
import com.example.configtracker.service.ConfigChangeServiceImpl;
import com.example.configtracker.service.NotificationService;
import com.example.configtracker.service.RuleTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigChangeServiceImplTest {

  @Mock
  private ConfigChangeRepo configChangeRepo;

  @Mock
  private NotificationService notificationService;

  @Mock
  private RuleTypeService ruleTypeService;

  @InjectMocks
  private ConfigChangeServiceImpl configChangeService;

  private ConfigChange sampleChange;
  private RuleType sampleRuleType;

  @BeforeEach
  void setUp() {
    sampleChange = new ConfigChange();
    sampleChange.setRuleTypeId(1L);
    sampleChange.setCurrentValue("42");
    sampleChange.setChangedBy("admin");
    sampleChange.setCritical(true);

    sampleRuleType = new RuleType();
    sampleRuleType.setId(1L);
    sampleRuleType.setName("MaxConnections");
    sampleRuleType.setValueType("INTEGER");
  }

  @Test
  void logChange_shouldSaveAndNotify_whenCriticalAndNoDuplicate() {
    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);
    when(configChangeRepo.findAll()).thenReturn(Collections.emptyList());
    when(configChangeRepo.generateId()).thenReturn(100L);

    ConfigChange result = configChangeService.logChange(sampleChange);

    assertEquals(100L, result.getId());
    assertNotNull(result.getChangedAt());
    verify(configChangeRepo).save(any(ConfigChange.class));
    verify(notificationService).notify(contains("Critical configuration change"));
  }

  @Test
  void logChange_shouldThrowAPIException_whenDuplicateExists() {
    ConfigChange duplicate = new ConfigChange();
    duplicate.setRuleTypeId(1L);
    duplicate.setCurrentValue("42");
    duplicate.setCritical(true);

    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);
    when(configChangeRepo.findAll()).thenReturn(List.of(duplicate));

    APIException exception = assertThrows(APIException.class, () ->
        configChangeService.logChange(sampleChange));

    assertTrue(exception.getMessage().contains("Duplicate not allowed"));
    verify(configChangeRepo, never()).save(any());
  }

  @Test
  void logChange_shouldThrowIllegalArgument_whenInvalidInteger() {
    sampleChange.setCurrentValue("abc");
    sampleRuleType.setValueType("INTEGER");
    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);
    when(configChangeRepo.findAll()).thenReturn(Collections.emptyList());

    assertThrows(IllegalArgumentException.class, () ->
        configChangeService.logChange(sampleChange));
  }

  @Test
  void logChange_shouldThrowIllegalArgument_whenInvalidBoolean() {
    sampleChange.setCurrentValue("yes");
    sampleRuleType.setValueType("BOOLEAN");
    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);
    when(configChangeRepo.findAll()).thenReturn(Collections.emptyList());

    assertThrows(IllegalArgumentException.class, () ->
        configChangeService.logChange(sampleChange));
  }

  @Test
  void getChangeById_shouldReturnChange_whenExists() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.of(sampleChange));

    ConfigChange result = configChangeService.getChangeById(1L);

    assertEquals(sampleChange, result);
    verify(configChangeRepo).findById(1L);
  }

  @Test
  void getChangeById_shouldThrowException_whenNotFound() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        configChangeService.getChangeById(1L));
  }

  @Test
  void delete_shouldDeleteAndReturn_whenExists() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.of(sampleChange));

    ConfigChange result = configChangeService.delete(1L);

    assertEquals(sampleChange, result);
    verify(configChangeRepo).deleteById(1L);
  }

  @Test
  void delete_shouldThrowException_whenNotFound() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        configChangeService.delete(1L));
  }

  @Test
  void listChanges_shouldReturnDTOList_whenChangesExist() {
    sampleChange.setChangedAt(LocalDateTime.now().minusDays(1));
    when(configChangeRepo.findAll()).thenReturn(List.of(sampleChange));
    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);

    List<ConfigChangeListDTO> result = configChangeService.listChanges(
        Optional.empty(), Optional.empty(), Optional.empty());

    assertEquals(1, result.size());
    assertEquals("MaxConnections", result.get(0).getRuleName());
  }

  @Test
  void listChanges_shouldThrowAPIException_whenNoChangesFound() {
    when(configChangeRepo.findAll()).thenReturn(Collections.emptyList());

    assertThrows(APIException.class, () ->
        configChangeService.listChanges(Optional.empty(), Optional.empty(), Optional.empty()));
  }

  @Test
  void testNotifyCalledForCriticalChange() {
    ConfigChange change = new ConfigChange();
    change.setRuleTypeId(1L);
    change.setCurrentValue("10");
    change.setChangedBy("admin");
    change.setCritical(true);

    when(ruleTypeService.getRuleTypeById(1L)).thenReturn(sampleRuleType);
    when(configChangeRepo.findAll()).thenReturn(Collections.emptyList());
    when(configChangeRepo.generateId()).thenReturn(1L);

    configChangeService.logChange(change);

    verify(notificationService, times(1))
        .notify(contains("Critical configuration change detected"));
  }
}
