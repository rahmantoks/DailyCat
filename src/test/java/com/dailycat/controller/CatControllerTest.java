package com.dailycat.controller;

import com.dailycat.model.Cat;
import com.dailycat.service.CatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CatController.class)
public class CatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatService catService;

    @Test
    void returnsCatOfTheDay() throws Exception {
        // stub implementation that returns a fixed cat
        when(catService.getRandomCat()).thenReturn(new Cat("id", "https://example.com/cat.jpg", null));

        mockMvc.perform(get("/api/cat-of-the-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/cat.jpg"));
    }
}
