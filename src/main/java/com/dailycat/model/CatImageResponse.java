package com.dailycat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Cat API /images/search and /images/{id} endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatImageResponse {
    private String id;
    private String url;
    private Integer width;
    private Integer height;
    
    @JsonProperty("breeds")
    private List<Breed> breeds;
}
