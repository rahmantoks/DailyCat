package com.dailycat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dog_breeds")
public class DogBreed {
    @Id
    private String id;

    private String name;
    private String temperament;

    @JsonProperty("bred_for")
    @Column(name = "bred_for")
    private String bredFor;

    @JsonProperty("breed_group")
    @Column(name = "breed_group")
    private String breedGroup;

    @JsonProperty("life_span")
    @Column(name = "life_span")
    private String lifeSpan;

    private String origin;

    // Dogs might not have the same numeric stats as cats in the API,
    // but we'll include common ones if available or map them differently later.
    // For now, keeping it simple based on typical Dog API response.

    @Column(length = 1000)
    private String description; // Often missing in Dog API, but good to have
}
