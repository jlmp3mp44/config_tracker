package com.example.configtracker.integration;

import com.example.configtracker.entities.ConfigChange;
import com.example.configtracker.entities.RuleType;
import com.example.configtracker.repo.RuleTypeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigChangeControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RuleTypeRepo ruleTypeRepo;

  @BeforeEach
  void setupRuleType() {
    ruleTypeRepo.findAll().clear();
    RuleType ruleType = new RuleType();
    ruleType.setName("MaxConnections");
    ruleType.setValueType("INTEGER");
    ruleTypeRepo.save(ruleType);
  }

  @Test
  void testCreateConfigChange() throws Exception {
    ConfigChange change = new ConfigChange();
    change.setRuleTypeId(ruleTypeRepo.findAll().get(0).getId());
    change.setCurrentValue("10");
    change.setChangedBy("admin");
    change.setCritical(false);

    MvcResult result = mockMvc.perform(post("/api/config-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(change)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentValue").value("10"))
        .andExpect(jsonPath("$.changedBy").value("admin"))
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    Number idNumber = JsonPath.read(responseBody, "$.id");
    long id = idNumber.longValue();

    mockMvc.perform(get("/api/config-changes/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentValue").value("10"))
        .andExpect(jsonPath("$.changedBy").value("admin"));
  }

  @Test
  void testListConfigChanges() throws Exception {
    ConfigChange change = new ConfigChange();
    change.setRuleTypeId(ruleTypeRepo.findAll().get(0).getId());
    change.setCurrentValue("20");
    change.setChangedBy("user");
    change.setCritical(true);

    mockMvc.perform(post("/api/config-changes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(change)));

    mockMvc.perform(get("/api/config-changes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].ruleName").value(hasItem("MaxConnections")));
  }

  @Test
  void testDeleteConfigChange() throws Exception {
    ConfigChange change = new ConfigChange();
    change.setRuleTypeId(ruleTypeRepo.findAll().get(0).getId());
    change.setCurrentValue("15");
    change.setChangedBy("tester");
    change.setCritical(false);

    MvcResult result = mockMvc.perform(post("/api/config-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(change)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    Number idNumber = JsonPath.read(responseBody, "$.id");
    long id = idNumber.longValue();

    mockMvc.perform(delete("/api/config-changes/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.changedBy").value("tester"));
  }
}
