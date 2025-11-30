package com.dailycat.service;

import com.dailycat.model.Breed;
import com.dailycat.model.Cat;
import com.dailycat.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatService {

    private final CatRepository catRepository;

    public Cat getRandomCat() {
        return catRepository.findRandomCat()
                .orElseGet(this::fallbackCat);
    }

    private Cat fallbackCat() {
        log.warn("No cats found in database, returning fallback");
        Cat cat = new Cat();
        cat.setId("fallback");
        cat.setImageUrl("https://cdn2.thecatapi.com/images/MTk3ODIyOQ.jpg");
        cat.setBreeds(List.of(
            Breed.builder()
                .id("beng")
                .name("Bengal")
                .temperament("Energetic, Intelligent, Gentle")
                .origin("United States")
                .lifeSpan("12-16 years")
                .wikipediaUrl("https://en.wikipedia.org/wiki/Bengal_(cat)")
                .build()
        ));
        return cat;
    }
}