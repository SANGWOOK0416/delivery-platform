package com.portfolio.weather.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAddressSearchResponse(List<Document> documents) {

    /**
     * Kakao returns coordinates as strings, with x = longitude and y = latitude.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(String x, String y) {
    }
}
