package com.dailycat.model;

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
    private String lifeSpan;
    private String wikipediaUrl;
    private Integer adaptability;
    private Integer energyLevel;
    private Integer affectionLevel;
    private Integer intelligence;
    private Integer vocalisation;
}
