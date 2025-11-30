package com.dailycat.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CatTest {

    @Test
    void gettersAndSettersWork() {
        Cat c = new Cat();
        c.setId("Whiskers");
        c.setImageUrl("https://example.com/w.jpg");
        c.setBreeds(List.of(new Breed()));

        assertEquals("Whiskers", c.getId());
        assertEquals("https://example.com/w.jpg", c.getImageUrl());
        assertNotNull(c.getBreeds());
    }

    @Test
    void constructorInitializesFields() {
        Cat c = new Cat("id", "https://example.com/m.jpg", null);
        assertEquals("id", c.getId());
        assertEquals("https://example.com/m.jpg", c.getImageUrl());
        assertNull(c.getBreeds());
    }
}
