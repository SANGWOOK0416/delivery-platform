package com.portfolio.notification.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kakao.api")
public record KakaoApiProperties(
        @NotBlank String token,
        @NotBlank String webUrl
) {
}
