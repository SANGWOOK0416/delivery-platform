package com.portfolio.weather.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.portfolio.weather.config.KakaoLocalApiProperties;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class KakaoGeocodingClientTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final KakaoLocalApiProperties properties =
            new KakaoLocalApiProperties("test-rest-api-key", "https://dapi.kakao.com");
    private final KakaoGeocodingClient client = new KakaoGeocodingClient(restTemplate, properties);

    @Test
    void throwsWhenRestApiKeyIsBlank() {
        KakaoGeocodingClient blankKeyClient =
                new KakaoGeocodingClient(restTemplate, new KakaoLocalApiProperties("", properties.baseUrl()));

        assertThatThrownBy(() -> blankKeyClient.geocode("서울시 강남구"))
                .isInstanceOf(GeocodingException.class);
    }

    @Test
    void parsesLatitudeAndLongitudeFromTheFirstDocument() {
        KakaoAddressSearchResponse response = new KakaoAddressSearchResponse(
                List.of(new KakaoAddressSearchResponse.Document("126.9780", "37.5665"))
        );
        when(restTemplate.exchange(any(URI.class), Mockito.eq(HttpMethod.GET), any(HttpEntity.class),
                Mockito.eq(KakaoAddressSearchResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        GeoCoordinate coordinate = client.geocode("서울시 중구 세종대로 110");

        assertThat(coordinate.latitude()).isEqualTo(37.5665);
        assertThat(coordinate.longitude()).isEqualTo(126.9780);
    }

    @Test
    void throwsWhenNoDocumentsMatch() {
        when(restTemplate.exchange(any(URI.class), Mockito.eq(HttpMethod.GET), any(HttpEntity.class),
                Mockito.eq(KakaoAddressSearchResponse.class)))
                .thenReturn(ResponseEntity.ok(new KakaoAddressSearchResponse(List.of())));

        assertThatThrownBy(() -> client.geocode("존재하지 않는 주소"))
                .isInstanceOf(GeocodingException.class);
    }

    @Test
    void throwsWhenTheHttpCallFails() {
        when(restTemplate.exchange(any(URI.class), Mockito.eq(HttpMethod.GET), any(HttpEntity.class),
                Mockito.eq(KakaoAddressSearchResponse.class)))
                .thenThrow(new RestClientException("network error"));

        assertThatThrownBy(() -> client.geocode("서울시 중구 세종대로 110"))
                .isInstanceOf(GeocodingException.class);
    }
}
