package com.dailycat.service;

import com.dailycat.model.Breed;
import com.dailycat.model.Cat;
import com.dailycat.model.CatImageResponse;
import com.dailycat.repository.BreedRepository;
import com.dailycat.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatSyncService {

    private final CatRepository catRepository;
    private final BreedRepository breedRepository;
    private final WebClient webClient;

    @Value("${catapi.api-key:}")
    private String apiKey;

    /**
     * Fetches 10 random cats from The Cat API every day at midnight
     * Cron: 0 0 0 * * * = every day at 00:00:00
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void syncCatsFromApi() {
        log.info("Starting daily cat sync from The Cat API");
        
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("CAT_API_KEY not set, skipping sync");
            return;
        }

        try {
            List<CatImageResponse> responses = fetchCatsFromApi(10);
            int savedCount = 0;

            for (CatImageResponse response : responses) {
                if (response != null && response.getId() != null) {
                    saveCatWithBreeds(response);
                    savedCount++;
                }
            }

            log.info("Successfully synced {} cats from The Cat API", savedCount);
        } catch (Exception e) {
            log.error("Error syncing cats from API", e);
        }
    }

    private List<CatImageResponse> fetchCatsFromApi(int limit) {
        CatImageResponse[] responses = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/images/search")
                    .queryParam("size", "med")
                    .queryParam("mime_types", "jpg,png")
                    .queryParam("order", "RANDOM")
                    .queryParam("limit", limit)
                    .queryParam("has_breeds", 1)
                    .build())
            .header("x-api-key", apiKey)
            .retrieve()
            .bodyToMono(CatImageResponse[].class)
            .block();

        return responses != null ? List.of(responses) : new ArrayList<>();
    }

    private void saveCatWithBreeds(CatImageResponse response) {
        // First, save or update breeds
        List<Breed> savedBreeds = new ArrayList<>();
        if (response.getBreeds() != null) {
            for (Breed breed : response.getBreeds()) {
                if (breed.getId() != null) {
                    // Only fetch and save if not existing in repository
                    String breedId = java.util.Objects.requireNonNull(breed.getId());
                    Breed existing = breedRepository.findById(breedId).orElse(null);
                    if (existing != null) {
                        savedBreeds.add(existing);
                    } else {
                        Breed fullBreed = fetchBreedDetails(breed.getId());
                        savedBreeds.add(breedRepository.save(fullBreed != null ? fullBreed : breed));
                    }
                }
            }
        }

        // Then save the cat with the breeds
        Cat cat = new Cat();
        cat.setId(response.getId());
        cat.setImageUrl(response.getUrl());
        cat.setBreeds(savedBreeds);
        catRepository.save(cat);

        log.debug("Saved cat {} with {} breeds", cat.getId(), savedBreeds.size());
    }

    private Breed fetchBreedDetails(String breedId) {
        try {
            return webClient.get()
                .uri("/breeds/{id}", breedId)
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(Breed.class)
                .block();
        } catch (Exception e) {
            log.warn("Could not fetch breed details for {}", breedId);
            return null;
        }
    }

    /**
     * Manual trigger for testing - fetches and saves cats immediately
     */
    @Transactional
    public int syncCatsNow(int count) {
        log.info("Manual sync requested for {} cats", count);
        
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CAT_API_KEY not configured");
        }

        List<CatImageResponse> responses = fetchCatsFromApi(count);
        int savedCount = 0;

        for (CatImageResponse response : responses) {
            if (response != null && response.getId() != null) {
                saveCatWithBreeds(response);
                savedCount++;
            }
        }

        return savedCount;
    }
}
