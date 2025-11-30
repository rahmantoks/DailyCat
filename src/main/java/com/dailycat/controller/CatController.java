package com.dailycat.controller;

import com.dailycat.model.Cat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatController {

    @GetMapping("/cat-of-the-day")
    public Cat getCatOfTheDay() {
        return new Cat(
                "Mochi",
                "https://cdn2.thecatapi.com/images/MTY3ODIyMQ.jpg",
                "A playful tuxedo cat who loves naps and laser pointers."
        );
    }
}