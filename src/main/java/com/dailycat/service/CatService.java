package com.dailycat.service;

import com.dailycat.model.Breed;
import com.dailycat.model.Cat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

            String catId = getCatId();
            System.out.println("Fetched cat image ID: " + catId);
            Cat cat = getCatById(catId);
            return cat;
        } catch (Exception ex) {
            return fallbackCat();
        }
    }

    private String getCatId(){
        // The Cat API: GET /images/search returns an array of image objects
        Mono<List<Map<String, Object>>> mono = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/images/search")
                    .queryParam("size", "med")
                    .queryParam("mime_types", "jpg,png")
                    .queryParam("order", "RANDOM")
                    .queryParam("limit", 1)
                    .queryParam("has_breeds", 1)
                    .build())
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {});

        List<Map<String, Object>> result = mono.block();
        if (result == null || result.isEmpty()) {
            return null;
        }
        Map<String, Object> first = result.get(0);
        return string(first.get("id"));
    }

    private Cat getCatById(String catId) {
        // The Cat API: GET /images/{image_id} to get more details
        Mono<Map<String, Object>> detailMono = webClient.get()
            .uri("/images/{id}", catId)
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> detail = detailMono.block();
        if (detail == null) {
            return null;
        } else {
            System.out.println("Fetched cat detail: " + detail);
            String breedId = null;
            Object breedsObj = detail.get("breeds");
            if (breedsObj instanceof List) {
                List<?> breedsList = (List<?>) breedsObj;
                if (!breedsList.isEmpty() && breedsList.get(0) instanceof Map) {
                    Map<String, Object> firstBreed = (Map<String, Object>) breedsList.get(0);
                    breedId = string(firstBreed.get("id"));
                }
            }

            Breed breed = getBreedsById(breedId);

            return new Cat(
                string(detail.get("id")),
                string(detail.get("url")),
                List.of(breed)
            );
        }
    }

    private Breed getBreedsById(String breedId) {
        // The Cat API: GET /breeds/{breed_id} to get breed details
        Mono<Map<String, Object>> breedMono = webClient.get()
            .uri("/breeds/{id}", breedId) // Example breed ID
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> breedDetail = breedMono.block();
        if (breedDetail != null) {
            System.out.println("Fetched breed detail: " + breedDetail);
            String id = string(breedDetail.get("id"));
            String name = string(breedDetail.get("name"));
            String temperament = string(breedDetail.get("temperament"));
            String origin = string(breedDetail.get("origin"));
            String lifeSpan = string(breedDetail.get("life_span"));
            String wikipediaUrl = string(breedDetail.get("wikipedia_url"));
            return Breed.builder()
                .id(id)
                .name(name)
                .temperament(temperament)
                .origin(origin)
                .lifeSpan(lifeSpan)
                .wikipediaUrl(wikipediaUrl)
                .build();
        } else {
            return null;
        }
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
                        .description("12-16 years")
                        .wikipediaUrl("https://en.wikipedia.org/wiki/Bengal_(cat)")
                        .build()
                )
        );
    }

    private String string(Object o) {
        return o == null ? null : o.toString();
    }
}