package com.portfolio.weather.client;

import com.portfolio.weather.config.KakaoLocalApiProperties;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Geocodes a free-text Korean address via the Kakao Local address-search API.
 * This uses the app's REST API key (Authorization: KakaoAK ...), not the
 * per-user OAuth access token that notification-service uses for message sends.
 */
@Component
@Profile("!loadtest")
@RequiredArgsConstructor
public class KakaoGeocodingClient implements GeocodingClient {

    private static final String AUTH_HEADER_PREFIX = "KakaoAK ";

    private final RestTemplate restTemplate;
    private final KakaoLocalApiProperties kakaoLocalApiProperties;

    @Override
    public GeoCoordinate geocode(String address) {
        if (!kakaoLocalApiProperties.hasRestApiKey()) {
            throw new GeocodingException("KAKAO_REST_API_KEY가 설정되지 않았습니다.");
        }

        KakaoAddressSearchResponse response;
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(kakaoLocalApiProperties.baseUrl())
                    .path("/v2/local/search/address.json")
                    .queryParam("query", address)
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + kakaoLocalApiProperties.restApiKey());

            response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers),
                    KakaoAddressSearchResponse.class).getBody();
        } catch (RestClientException exception) {
            throw new GeocodingException("카카오 로컬 API 호출 실패: " + exception.getMessage(), exception);
        }

        if (response == null || response.documents() == null || response.documents().isEmpty()) {
            throw new GeocodingException("주소 검색 결과가 없습니다: " + address);
        }

        KakaoAddressSearchResponse.Document document = response.documents().get(0);
        try {
            double longitude = Double.parseDouble(document.x());
            double latitude = Double.parseDouble(document.y());
            return new GeoCoordinate(latitude, longitude);
        } catch (NumberFormatException exception) {
            throw new GeocodingException("주소 검색 응답의 좌표를 파싱할 수 없습니다.", exception);
        }
    }
}
