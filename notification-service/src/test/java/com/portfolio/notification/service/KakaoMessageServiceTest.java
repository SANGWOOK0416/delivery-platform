package com.portfolio.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.notification.config.KakaoApiProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class KakaoMessageServiceTest {

    @Test
    void sendsAFormEncodedTemplateWithABearerToken() {
        RestTemplate restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        KakaoMessageService service = new KakaoMessageService(
                restTemplate,
                new ObjectMapper(),
                new KakaoApiProperties("test-token", "https://example.com/orders")
        );
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        service.sendDeliveryAlert(new DeliveryRiskEvent(101L, "Seoul, Mapo-gu", 1));

        ArgumentCaptor<HttpEntity<?>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(
                eq("https://kapi.kakao.com/v2/api/talk/memo/default/send"),
                requestCaptor.capture(),
                eq(Void.class)
        );

        assertThat(requestCaptor.getValue().getHeaders().getFirst("Authorization"))
                .isEqualTo("Bearer test-token");
        assertThat(requestCaptor.getValue().getBody().toString())
                .contains("Seoul, Mapo-gu")
                .contains("Rain");
    }
}
