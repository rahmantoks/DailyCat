package com.dailycat.service;

import com.dailycat.model.Dog;
import com.dailycat.model.DogBreed;

import com.dailycat.repository.DogBreedRepository;
import com.dailycat.repository.DogRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DogSyncService {

    @Qualifier("dogWebClient")
    private final WebClient dogWebClient;

    private final DogRepository dogRepository;
    private final DogBreedRepository dogBreedRepository;

    @Value("${dogapi.api-key:}")
    private String apiKey;

    @Transactional
    public int syncDogsNow(int limit) {
        log.info("Starting manual dog sync for {} items...", limit);
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("DOG_API_KEY is not set. Sync might fail or be limited.");
        }

        try {
            List<DogImageResponse> responses = dogWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/images/search")
                            .queryParam("limit", limit)
                            .queryParam("has_breeds", 1)
                            .queryParam("order", "RANDOM")
                            .build())
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .bodyToFlux(DogImageResponse.class)
                    .collectList()
                    .block();

            if (responses == null || responses.isEmpty()) {
                log.warn("No dogs received from API");
                return 0;
            }

            int savedCount = 0;
            for (DogImageResponse resp : responses) {
                if (resp.getBreeds() == null || resp.getBreeds().isEmpty()) {
                    continue;
                }
                saveDog(resp);
                savedCount++;
            }
            log.info("Synced {} dogs successfully.", savedCount);
            return savedCount;
        } catch (Exception e) {
            log.error("Error syncing dogs", e);
            throw e;
        }
    }

    private void saveDog(DogImageResponse resp) {
        Dog dog = new Dog();
        dog.setId(resp.getId());
        dog.setImageUrl(resp.getUrl());

        List<DogBreed> breeds = new ArrayList<>();
        for (DogBreedDto breedDto : resp.getBreeds()) {
            DogBreed breed = dogBreedRepository.findById(String.valueOf(breedDto.getId()))
                    .orElseGet(() -> {
                        DogBreed newBreed = new DogBreed();
                        newBreed.setId(String.valueOf(breedDto.getId()));
                        newBreed.setName(breedDto.getName());
                        newBreed.setTemperament(breedDto.getTemperament());
                        newBreed.setBredFor(breedDto.getBredFor());
                        newBreed.setBreedGroup(breedDto.getBreedGroup());
                        newBreed.setLifeSpan(breedDto.getLifeSpan());
                        newBreed.setOrigin(breedDto.getOrigin());
                        // Description is not standard in Dog API breed object usually
                        return dogBreedRepository.save(newBreed);
                    });
            breeds.add(breed);
        }
        dog.setBreeds(breeds);
        dogRepository.save(dog);
    }

    // DTOs specific to Dog API response
    @Data
    static class DogImageResponse {
        private String id;
        private String url;
        private Integer width;
        private Integer height;
        private List<DogBreedDto> breeds;
    }

    @Data
    static class DogBreedDto {
        private Integer id; // Dog API uses integer IDs for breeds
        private String name;
        private String temperament;
        @JsonProperty("bred_for")
        private String bredFor;
        @JsonProperty("breed_group")
        private String breedGroup;
        @JsonProperty("life_span")
        private String lifeSpan;
        private String origin;
    }
}
