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
@Table(name = "breeds")
public class Breed {
    @Id
    private String id;
    
    private String name;
    private String temperament;
    private String origin;
    
    @Column(length = 1000)
    private String description;
    
    @JsonProperty("life_span")
    @Column(name = "life_span")
    private String lifeSpan;
    
    @JsonProperty("wikipedia_url")
    @Column(name = "wikipedia_url")
    private String wikipediaUrl;
    
    private Integer adaptability;
    
    @JsonProperty("energy_level")
    @Column(name = "energy_level")
    private Integer energyLevel;
    
    @JsonProperty("affection_level")
    @Column(name = "affection_level")
    private Integer affectionLevel;
    
    private Integer intelligence;
    private Integer vocalisation;
}
