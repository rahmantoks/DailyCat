package com.dailycat.service;

import com.dailycat.model.DogBreed;
import com.dailycat.model.Dog;
import com.dailycat.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;

    public Dog getRandomDog() {
        return dogRepository.findRandomDog()
                .orElseGet(this::fallbackDog);
    }

    private Dog fallbackDog() {
        log.warn("No dogs found in database, returning fallback");
        Dog dog = new Dog();
        dog.setId("fallback-dog");
        dog.setImageUrl("https://cdn2.thedogapi.com/images/BJa4kxc4X.jpg");
        dog.setBreeds(List.of(
                DogBreed.builder()
                        .id("afgh")
                        .name("Afghan Hound")
                        .temperament("Aloof, Clownish, Dignified, Independent, Happy")
                        .origin("Afghanistan, Iran, Pakistan")
                        .lifeSpan("10 - 13 years")
                        .bredFor("Coursing and hunting")
                        .breedGroup("Hound")
                        .build()));
        return dog;
    }
}
