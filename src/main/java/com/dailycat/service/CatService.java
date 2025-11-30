package com.dailycat.service;

import com.dailycat.model.Breeds;
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
                return fallbackCat();
            }
            Map<String, Object> item = result.get(0);
            String id = string(item.get("id"));
            System.out.println("Fetched cat image ID: " + id);
            // The Cat API: GET /images/{image_id} to get more details
            Mono<Map<String, Object>> detailMono = webClient.get()
                .uri("/images/{id}", id)
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> detail = detailMono.block();
            System.out.println("Fetched cat detail: " + detail);
            if (detail == null) {
                return fallbackCat();
            }
            String imageUrl = string(detail.get("url"));
            Breeds breeds = breedsFromDetail(detail);
            String name = breeds != null ? breeds.getName() : "Unknown Cat";
            String description = breeds != null ? breeds.getTemperament() : "No description available.";
            if (imageUrl == null || imageUrl.isBlank()) {
                return fallbackCat();
            }
            return new Cat(id, name, imageUrl, description, breeds);
        } catch (Exception ex) {
            return fallbackCat();
        }
    }

    private Breeds breedsFromDetail(Map<String,Object> detail) {
        Object breedsObj = detail.get("breeds");
        if (breedsObj instanceof List) {
            List<?> breedsList = (List<?>) breedsObj;
            if (!breedsList.isEmpty() && breedsList.get(0) instanceof Map) {
                Map<String, Object> breedMap = (Map<String, Object>) breedsList.get(0);
                String id = string(breedMap.get("id"));
                String name = string(breedMap.get("name"));
                String temperament = string(breedMap.get("temperament"));
                String origin = string(breedMap.get("origin"));
                String description = string(breedMap.get("description"));
                String lifeSpan = string(breedMap.get("life_span"));
                String wikipediaUrl = string(breedMap.get("wikipedia_url"));
                return new Breeds(id, name, temperament, origin, description, lifeSpan, wikipediaUrl);
            }
        }
        return null;
    }

    private Cat fallbackCat() {
        return new Cat(
                "id",
                "Luna",
                "https://cdn2.thecatapi.com/images/MTk3ODIyOQ.jpg",
                "Curious tabby cat who adores window watching and treats.",
                new Breeds("beng", "Bengal", "Energetic, Intelligent, Gentle", "United States", "The Bengal is a domesticated cat breed created from hybrids of domestic cats and the Asian leopard cat.", "12-16 years", "https://en.wikipedia.org/wiki/Bengal_(cat)")
        );
    }

    private String string(Object o) {
        return o == null ? null : o.toString();
    }
}