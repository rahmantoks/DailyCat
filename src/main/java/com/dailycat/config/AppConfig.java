package com.dailycat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public WebClient webClient(@Value("${catapi.base-url:https://api.thecatapi.com/v1}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl == null ? "https://api.thecatapi.com/v1" : baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    @Bean
    public WebClient dogWebClient(@Value("${dogapi.base-url:https://api.thedogapi.com/v1}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl == null ? "https://api.thedogapi.com/v1" : baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }
}
