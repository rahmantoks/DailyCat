package com.dailycat.service;

import com.dailycat.model.Cat;
import com.dailycat.repository.CatRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class CatServiceTest {

    @Mock
    private CatRepository catRepository = org.mockito.Mockito.mock(CatRepository.class);
    
    private CatService catService = new CatService(catRepository);

    @Test
    void returnsFallbackWhenApiKeyMissing() {
        Cat cat = catService.getRandomCat();
        assertNotNull(cat);
        assertEquals("id", cat.getId());
        assertNotNull(cat.getImageUrl());
    }

    // Additional tests can be added here to cover more scenarios
}
