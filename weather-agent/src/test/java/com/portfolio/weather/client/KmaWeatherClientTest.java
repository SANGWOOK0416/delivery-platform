package com.portfolio.weather.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.weather.config.KmaApiProperties;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class KmaWeatherClientTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final KmaApiProperties properties = new KmaApiProperties(
            "test-service-key",
            "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0",
            new KmaApiProperties.Grid(60, 127)
    );
    private final KmaWeatherClient client = new KmaWeatherClient(restTemplate, properties);

    @Test
    void returnsNoPrecipitationWithoutCallingKmaWhenServiceKeyIsBlank() {
        KmaApiProperties blankKeyProperties = new KmaApiProperties("", properties.baseUrl(), properties.grid());
        KmaWeatherClient blankKeyClient = new KmaWeatherClient(restTemplate, blankKeyProperties);

        int precipitationType = blankKeyClient.fetchCurrentPrecipitationType(60, 127);

        assertThat(precipitationType).isZero();
        verify(restTemplate, never()).getForObject(any(URI.class), any());
    }

    @Test
    void extractsThePtyValueFromASuccessfulResponse() {
        when(restTemplate.getForObject(any(URI.class), Mockito.eq(KmaUltraSrtNcstResponse.class)))
                .thenReturn(successResponseWithPty("1"));

        int precipitationType = client.fetchCurrentPrecipitationType(60, 127);

        assertThat(precipitationType).isEqualTo(1);
    }

    @Test
    void fallsBackToNoPrecipitationWhenKmaCallFails() {
        when(restTemplate.getForObject(any(URI.class), Mockito.eq(KmaUltraSrtNcstResponse.class)))
                .thenThrow(new RestClientException("network error"));

        int precipitationType = client.fetchCurrentPrecipitationType(60, 127);

        assertThat(precipitationType).isZero();
    }

    @Test
    void fallsBackToNoPrecipitationWhenKmaReturnsAnErrorResultCode() {
        when(restTemplate.getForObject(any(URI.class), Mockito.eq(KmaUltraSrtNcstResponse.class)))
                .thenReturn(new KmaUltraSrtNcstResponse(new KmaUltraSrtNcstResponse.Response(
                        new KmaUltraSrtNcstResponse.Header("30", "SERVICE_KEY_IS_NOT_REGISTERED_ERROR"),
                        null
                )));

        int precipitationType = client.fetchCurrentPrecipitationType(60, 127);

        assertThat(precipitationType).isZero();
    }

    private KmaUltraSrtNcstResponse successResponseWithPty(String ptyValue) {
        return new KmaUltraSrtNcstResponse(new KmaUltraSrtNcstResponse.Response(
                new KmaUltraSrtNcstResponse.Header("00", "NORMAL_SERVICE"),
                new KmaUltraSrtNcstResponse.Body(new KmaUltraSrtNcstResponse.Items(List.of(
                        new KmaUltraSrtNcstResponse.Item("T1H", "21"),
                        new KmaUltraSrtNcstResponse.Item("PTY", ptyValue)
                )))
        ));
    }
}
