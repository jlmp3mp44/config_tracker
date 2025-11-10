package com.example.configtracker.service;

import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.exception.APIException;
import com.example.configtracker.exception.ResourceNotFoundException;
import com.example.configtracker.repo.ConfigChangeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigChangeServiceImplTest {

  @Mock
  private ConfigChangeRepo configChangeRepo;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private ConfigChangeServiceImpl configChangeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // ---- TEST 1 ----
  @Test
  void testLogChange_SavesChangeAndNotifiesIfCritical() {
    // given
    ConfigChange change = new ConfigChange();
    change.setRuleType("APPROVAL_LIMIT");
    change.setCritical(true);

    when(configChangeRepo.generateId()).thenReturn(1L);
    when(configChangeRepo.save(any())).thenReturn(change);

    // when
    ConfigChange result = configChangeService.logChange(change);

    // then
    verify(configChangeRepo).save(change);
    verify(notificationService).notify(contains("Critical configuration change detected"));
    assertNotNull(result.getChangedAt());
    assertEquals(1L, result.getId());
  }

  // ---- TEST 2 ----
  @Test
  void testLogChange_NoNotificationIfNotCritical() {
    ConfigChange change = new ConfigChange();
    change.setRuleType("CREDIT_LIMIT");
    change.setCritical(false);

    when(configChangeRepo.generateId()).thenReturn(1L);

    ConfigChange result = configChangeService.logChange(change);

    verify(configChangeRepo).save(change);
    verify(notificationService, never()).notify(any());
    assertEquals(1L, result.getId());
  }

  // ---- TEST 3 ----
  @Test
  void testListChanges_FiltersCorrectly() {
    ConfigChange a = new ConfigChange(1L, "CREDIT_LIMIT", "10", "20", "admin", LocalDateTime.now(), false);
    ConfigChange b = new ConfigChange(2L, "APPROVAL_RULE", "on", "off", "user", LocalDateTime.now(), true);

    when(configChangeRepo.findAll()).thenReturn(List.of(a, b));

    List<ConfigChange> result = configChangeService.listChanges(Optional.of("credit_limit"), Optional.empty(), Optional.empty());

    assertEquals(1, result.size());
    assertEquals("CREDIT_LIMIT", result.get(0).getRuleType());
  }

  // ---- TEST 4 ----
  @Test
  void testListChanges_ThrowsExceptionWhenEmpty() {
    when(configChangeRepo.findAll()).thenReturn(List.of());

    assertThrows(APIException.class,
        () -> configChangeService.listChanges(Optional.empty(), Optional.empty(), Optional.empty()));
  }

  // ---- TEST 5 ----
  @Test
  void testGetChangeById_ReturnsChange() {
    ConfigChange change = new ConfigChange();
    change.setId(1L);
    when(configChangeRepo.findById(1L)).thenReturn(Optional.of(change));

    ConfigChange result = configChangeService.getChangeById(1L);

    assertEquals(1L, result.getId());
  }

  // ---- TEST 6 ----
  @Test
  void testGetChangeById_NotFound() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> configChangeService.getChangeById(1L));
  }

  // ---- TEST 7 ----
  @Test
  void testUpdate_CreatesNewVersionAndNotifiesIfCritical() {
    ConfigChange old = new ConfigChange(1L, "APPROVAL_RULE", "yes", "no", "admin", LocalDateTime.now(), false);
    ConfigChange update = new ConfigChange();
    update.setCurrentValue("maybe");
    update.setChangedBy("user");

    when(configChangeRepo.findById(1L)).thenReturn(Optional.of(old));
    when(configChangeRepo.generateId()).thenReturn(2L);

    ConfigChange result = configChangeService.update(1L, update);

    verify(configChangeRepo).save(any(ConfigChange.class));
    verify(notificationService).notify(contains("Critical configuration change detected"));
    assertEquals("APPROVAL_RULE", result.getRuleType());
  }

  // ---- TEST 8 ----
  @Test
  void testDelete_RemovesChange() {
    ConfigChange change = new ConfigChange();
    change.setId(1L);
    when(configChangeRepo.findById(1L)).thenReturn(Optional.of(change));

    ConfigChange deleted = configChangeService.delete(1L);

    verify(configChangeRepo).deleteById(1L);
    assertEquals(1L, deleted.getId());
  }

  @Test
  void testDelete_NotFound() {
    when(configChangeRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> configChangeService.delete(1L));
  }
}
