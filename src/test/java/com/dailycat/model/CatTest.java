package com.dailycat.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CatTest {

    @Test
    void gettersAndSettersWork() {
        Cat c = new Cat();
        c.setName("Whiskers");
        c.setImageUrl("https://example.com/w.jpg");
        c.setDescription("Friendly cat");
        c.setBreeds(new Breeds());

        assertEquals("Whiskers", c.getName());
        assertEquals("https://example.com/w.jpg", c.getImageUrl());
        assertEquals("Friendly cat", c.getDescription());
        assertNotNull(c.getBreeds());
    }

    @Test
    void constructorInitializesFields() {
        Cat c = new Cat("id", "Mochi", "https://example.com/m.jpg", "Playful", null);
        assertEquals("Mochi", c.getName());
        assertEquals("https://example.com/m.jpg", c.getImageUrl());
        assertEquals("Playful", c.getDescription());
        assertNull(c.getBreeds());
    }
}
