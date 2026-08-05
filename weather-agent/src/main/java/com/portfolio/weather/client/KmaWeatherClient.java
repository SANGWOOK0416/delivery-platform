package com.portfolio.weather.client;

import com.portfolio.weather.config.KmaApiProperties;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Looks up the current precipitation type (PTY) from the KMA 초단기실황(getUltraSrtNcst) API
 * for a given forecast grid cell.
 */
@Slf4j
@Component
@Profile("!loadtest")
@RequiredArgsConstructor
public class KmaWeatherClient implements WeatherApiClient {

    private static final DateTimeFormatter BASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter BASE_TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    private static final int PUBLISH_DELAY_MINUTES = 40;
    private static final String SUCCESS_RESULT_CODE = "00";
    private static final String PTY_CATEGORY = "PTY";
    private static final int NO_PRECIPITATION = 0;

    private final RestTemplate restTemplate;
    private final KmaApiProperties kmaApiProperties;

    @Override
    public int fetchCurrentPrecipitationType(int nx, int ny) {
        if (!kmaApiProperties.hasServiceKey()) {
            log.warn("KMA_SERVICE_KEY is not configured; skipping live weather lookup.");
            return NO_PRECIPITATION;
        }

        try {
            KmaUltraSrtNcstResponse response = requestUltraSrtNcst(nx, ny);
            return extractPrecipitationType(response);
        } catch (Exception exception) {
            // A flaky external API must never block order processing — fall back safely and move on.
            log.error("Failed to fetch weather data from KMA. Falling back to no-precipitation.", exception);
            return NO_PRECIPITATION;
        }
    }

    private KmaUltraSrtNcstResponse requestUltraSrtNcst(int nx, int ny) {
        LocalDateTime baseDateTime = resolveBaseDateTime(LocalDateTime.now());

        URI uri = UriComponentsBuilder.fromHttpUrl(kmaApiProperties.baseUrl())
                .path("/getUltraSrtNcst")
                .queryParam("serviceKey", kmaApiProperties.serviceKey())
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime.format(BASE_DATE_FORMAT))
                .queryParam("base_time", baseDateTime.format(BASE_TIME_FORMAT))
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build()
                .encode()
                .toUri();

        KmaUltraSrtNcstResponse response = restTemplate.getForObject(uri, KmaUltraSrtNcstResponse.class);
        if (response == null || response.response() == null || response.response().header() == null) {
            throw new IllegalStateException("Empty response from KMA API");
        }

        String resultCode = response.response().header().resultCode();
        if (!SUCCESS_RESULT_CODE.equals(resultCode)) {
            throw new IllegalStateException(
                    "KMA API returned an error: " + resultCode + " " + response.response().header().resultMsg());
        }

        return response;
    }

    private int extractPrecipitationType(KmaUltraSrtNcstResponse response) {
        return response.response().body().items().item().stream()
                .filter(item -> PTY_CATEGORY.equals(item.category()))
                .map(KmaUltraSrtNcstResponse.Item::obsrValue)
                .findFirst()
                .map(Integer::parseInt)
                .orElseGet(() -> {
                    log.warn("PTY category missing from KMA response; defaulting to no precipitation.");
                    return NO_PRECIPITATION;
                });
    }

    private LocalDateTime resolveBaseDateTime(LocalDateTime now) {
        LocalDateTime onTheHour = now.withMinute(0).withSecond(0).withNano(0);
        // getUltraSrtNcst for base_time HH:00 isn't published until roughly HH:40.
        return now.getMinute() < PUBLISH_DELAY_MINUTES ? onTheHour.minusHours(1) : onTheHour;
    }
}
