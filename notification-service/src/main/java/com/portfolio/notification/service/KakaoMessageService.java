package com.portfolio.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.notification.config.KakaoApiProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Profile("!loadtest")
@RequiredArgsConstructor
public class KakaoMessageService implements KakaoMessageSender {

    private static final String KAKAO_MEMO_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoApiProperties kakaoApiProperties;

    @Override
    public void sendDeliveryAlert(DeliveryRiskEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoApiProperties.token());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", createTemplateObject(event));

        try {
            restTemplate.postForEntity(
                    KAKAO_MEMO_URL,
                    new HttpEntity<>(body, headers),
                    Void.class
            );
            log.info("Sent Kakao delivery alert. orderId={}", event.orderId());
        } catch (RestClientException exception) {
            log.error("Failed to send Kakao delivery alert. orderId={}", event.orderId(), exception);
            throw exception;
        }
    }

    private String createTemplateObject(DeliveryRiskEvent event) {
        Map<String, Object> template = Map.of(
                "object_type", "text",
                "text", "[Delivery platform update]\nOrder: " + event.orderId()
                        + "\nAddress: " + event.address()
                        + "\nWeather: " + weatherDescription(event.precipitationType()),
                "link", Map.of(
                        "web_url", kakaoApiProperties.webUrl(),
                        "mobile_web_url", kakaoApiProperties.webUrl()
                )
        );

        try {
            return objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize Kakao message template", exception);
        }
    }

    private String weatherDescription(int precipitationType) {
        return switch (precipitationType) {
            case 0 -> "Clear";
            case 1 -> "Rain";
            case 2 -> "Rain and snow";
            case 3 -> "Snow";
            case 4 -> "Shower";
            default -> "Unknown";
        };
    }
}
