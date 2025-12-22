package com.dailycat.controller;

import com.dailycat.model.Dog;
import com.dailycat.service.DogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping("/dog-of-the-day")
    public Dog getDogOfTheDay() {
        return dogService.getRandomDog();
    }
}
