package com.agenticfinance.web;

import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PortfolioControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    PortfolioService portfolioService;

    private String portfolioId;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
        Portfolio p = portfolioService.create(BigDecimal.valueOf(200), "USD");
        portfolioService.addDeposit(p.getId(), BigDecimal.valueOf(50));
        portfolioId = p.getId();
    }

    @Test
    void getCapitalState() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio/{id}/capital-state", portfolioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preservedBase").value(250))
                .andExpect(jsonPath("$.totalCapital").value(250));
    }

    @Test
    void getVerifiedStatus() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio/{id}/status", portfolioId).param("queryType", "summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.portfolio.totalCapital").value(250));
    }
}
