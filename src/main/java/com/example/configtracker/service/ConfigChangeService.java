package com.example.configtracker.service;

import com.example.configtracker.dto.ConfigChangeListDTO;
import com.example.configtracker.dto.ConfigChangeUpdateRequest;
import com.example.configtracker.entities.ConfigChange;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface
ConfigChangeService {

  public ConfigChange logChange(ConfigChange change);
  public List<ConfigChangeListDTO> listChanges(Optional<String> type, Optional<LocalDateTime> from, Optional<LocalDateTime> to);
  public ConfigChange getChangeById(Long id);

  ConfigChange updateByRuleId(Long id, ConfigChangeUpdateRequest updatedChange);

  ConfigChange delete(Long id);
}
