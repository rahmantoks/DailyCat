package com.dailycat.controller;

import com.dailycat.model.Cat;
import com.dailycat.service.CatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CatController.class)
@Import(CatControllerTest.TestConfig.class)
public class CatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsCatOfTheDay() throws Exception {
        // TestConfig provides a stub implementation that returns a fixed cat

        mockMvc.perform(get("/api/cat-of-the-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Unit Cat"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/cat.jpg"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CatService catService() {
            return new com.dailycat.service.CatService(org.springframework.web.reactive.function.client.WebClient.builder().baseUrl("http://localhost").build(), "") {
                @Override
                public Cat getRandomCat() {
                    return new Cat("id", "Unit Cat", "https://example.com/cat.jpg", "Test cat");
                }
            };
        }
    }
}
