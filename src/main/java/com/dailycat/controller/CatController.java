package com.dailycat.controller;

import com.dailycat.model.Cat;
import com.dailycat.service.CatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    @GetMapping("/cat-of-the-day")
    public Cat getCatOfTheDay() {
        return catService.getRandomCat();
    }
}