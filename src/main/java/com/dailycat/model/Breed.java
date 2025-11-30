package com.dailycat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Breed {
    private String id;
    private String name;
    private String temperament;
    private String origin;
    private String description;
    
    @JsonProperty("life_span")
    private String lifeSpan;
    
    @JsonProperty("wikipedia_url")
    private String wikipediaUrl;
    
    private Integer adaptability;
    
    @JsonProperty("energy_level")
    private Integer energyLevel;
    
    @JsonProperty("affection_level")
    private Integer affectionLevel;
    
    private Integer intelligence;
    private Integer vocalisation;
}
