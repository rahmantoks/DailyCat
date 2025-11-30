package com.dailycat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Breeds {
    private String id;
    private String name;
    private String temperament;
    private String origin;
    private String description;
    private String lifeSpan;
    private String wikipediaUrl;
}
