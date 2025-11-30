package com.dailycat.service;

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

            // The Cat API: GET /images/{image_id} to get more details
            Mono<Map<String, Object>> detailMono = webClient.get()
                .uri("/images/{id}", id)
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> detail = detailMono.block();
            if (detail == null) {
                return fallbackCat();
            }
            String imageUrl = string(detail.get("url"));
            String name = "Random Cat";
            String description = "Fetched from The Cat API";
            if (imageUrl == null || imageUrl.isBlank()) {
                return fallbackCat();
            }
            return new Cat(id, name, imageUrl, description);
        } catch (Exception ex) {
            return fallbackCat();
        }
    }

    private Cat fallbackCat() {
        return new Cat(
                "id",
                "Luna",
                "https://cdn2.thecatapi.com/images/MTk3ODIyOQ.jpg",
                "Curious tabby cat who adores window watching and treats."
        );
    }

    private String string(Object o) {
        return o == null ? null : o.toString();
    }
}