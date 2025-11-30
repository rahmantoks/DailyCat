package com.dailycat.service;

import com.dailycat.model.Breed;
import com.dailycat.model.Cat;
import com.dailycat.model.CatImageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class CatService {

    private final WebClient webClient;
    private final String apiKey;

    @Autowired
    public CatService(
        @Value("${catapi.base-url:https://api.thecatapi.com/v1}") String baseUrl,
        @Value("${catapi.api-key:}") String apiKey
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl == null ? "https://api.thecatapi.com/v1" : baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
        this.apiKey = apiKey;
    }

    // Additional constructor for unit testing (inject mock WebClient)
    public CatService(WebClient webClient, String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public Cat getRandomCat() {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                return fallbackCat();
            }

            CatImageResponse imageResponse = getCatImageWithBreed();
            if (imageResponse == null || imageResponse.getBreeds() == null || imageResponse.getBreeds().isEmpty()) {
                return fallbackCat();
            }
            
            String breedId = imageResponse.getBreeds().get(0).getId();
            System.out.println("Fetched breed ID: " + breedId);
            Breed breedDetail = getBreedById(breedId);
            System.out.println("Fetched breed detail: " + breedDetail);
            
            return new Cat(
                imageResponse.getId(),
                imageResponse.getUrl(),
                breedDetail != null ? List.of(breedDetail) : imageResponse.getBreeds()
            );
        } catch (Exception ex) {
            return fallbackCat();
        }
    }

    private CatImageResponse getCatImageWithBreed() {
        CatImageResponse[] responses = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/images/search")
                    .queryParam("size", "med")
                    .queryParam("mime_types", "jpg,png")
                    .queryParam("order", "RANDOM")
                    .queryParam("limit", 1)
                    .queryParam("has_breeds", 1)
                    .build())
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(CatImageResponse[].class)
            .block();

        return (responses != null && responses.length > 0) ? responses[0] : null;
    }

    private Breed getBreedById(String breedId) {
        if (breedId == null || breedId.isBlank()) {
            return null;
        }
        
        return webClient.get()
            .uri("/breeds/{id}", breedId)
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(Breed.class)
            .block();
    }

    private Cat fallbackCat() {
        return new Cat(
                "id",
                "https://cdn2.thecatapi.com/images/MTk3ODIyOQ.jpg",
                List.of(
                    Breed.builder()
                        .id("beng")
                        .name("Bengal")
                        .temperament("Energetic, Intelligent, Gentle")
                        .origin("United States")
                        .lifeSpan("12-16 years")
                        .wikipediaUrl("https://en.wikipedia.org/wiki/Bengal_(cat)")
                        .build()
                )
        );
    }
}