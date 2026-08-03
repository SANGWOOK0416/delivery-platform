package com.portfolio.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kma.api")
public record KmaApiProperties(String serviceKey, String baseUrl, Grid grid) {

    public record Grid(int nx, int ny) {
    }

    public boolean hasServiceKey() {
        return serviceKey != null && !serviceKey.isBlank();
    }
}
