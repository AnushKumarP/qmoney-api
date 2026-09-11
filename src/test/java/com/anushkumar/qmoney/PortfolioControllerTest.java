package com.anushkumar.qmoney;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {
  @Autowired MockMvc mockMvc;

  @Test
  void sampleReturnsRankedPortfolio() throws Exception {
    mockMvc.perform(get("/api/portfolio/sample"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.returns.length()").value(3))
        .andExpect(jsonPath("$.returns[0].symbol").isNotEmpty());
  }
}
