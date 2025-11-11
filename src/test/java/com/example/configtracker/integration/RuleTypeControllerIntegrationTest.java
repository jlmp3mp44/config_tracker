package com.example.configtracker.integration;

import com.example.configtracker.entities.RuleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RuleTypeControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private RuleType ruleType;

  @BeforeEach
  void setUp() {
    ruleType = new RuleType();
    ruleType.setName("MaxConnections");
    ruleType.setValueType("INTEGER");
  }

  @Test
  void testCreateAndGetRuleType() throws Exception {

    MvcResult createResult = mockMvc.perform(post("/api/rule-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ruleType)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("MaxConnections"))
        .andExpect(jsonPath("$.valueType").value("INTEGER"))
        .andReturn();

    String responseBody = createResult.getResponse().getContentAsString();
    Number idNumber = JsonPath.read(responseBody, "$.id");
    long id = idNumber.longValue();

    mockMvc.perform(get("/api/rule-types/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("MaxConnections"))
        .andExpect(jsonPath("$.valueType").value("INTEGER"));

    mockMvc.perform(get("/api/rule-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItem("MaxConnections")));
  }


  @Test
  void testUpdateRuleType() throws Exception {

    mockMvc.perform(post("/api/rule-types")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(ruleType)));

    RuleType updated = new RuleType();
    updated.setName("ConnectionLimit");
    updated.setValueType("INTEGER");

    mockMvc.perform(put("/api/rule-types/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("ConnectionLimit"));
  }

  @Test
  void testDeleteRuleType() throws Exception {

    MvcResult result = mockMvc.perform(post("/api/rule-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"MaxConnections\",\"valueType\":\"INTEGER\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("MaxConnections"))
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    Number idNumber = JsonPath.read(responseBody, "$.id");
    long id = idNumber.longValue();

    mockMvc.perform(delete("/api/rule-types/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("MaxConnections"));
  }

}
