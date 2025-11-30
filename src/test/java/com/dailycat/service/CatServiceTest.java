package com.dailycat.service;

import com.dailycat.model.Cat;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

public class CatServiceTest {

    @Test
    void returnsFallbackWhenApiKeyMissing() {
        WebClient webClient = WebClient.builder().baseUrl("https://api.thecatapi.com/v1").build();
        CatService service = new CatService(webClient, "");
        Cat cat = service.getRandomCat();
        assertNotNull(cat);
        assertEquals("Luna", cat.getName());
        assertNotNull(cat.getImageUrl());
    }

    @Test
    void returnsFallbackWhenApiCallFails() {
        // Use an invalid base URL to force connection failure
        WebClient webClient = WebClient.builder().baseUrl("http://127.0.0.1:9").build();
        CatService service = new CatService(webClient, "fake-key");
        Cat cat = service.getRandomCat();
        assertNotNull(cat);
        assertEquals("Luna", cat.getName());
        assertNotNull(cat.getImageUrl());
    }
}
