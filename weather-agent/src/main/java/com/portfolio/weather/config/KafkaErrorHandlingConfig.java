package com.portfolio.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    private static final long INITIAL_BACKOFF_INTERVAL_MS = 500L;
    private static final double BACKOFF_MULTIPLIER = 2.0;
    private static final int MAX_ATTEMPTS = 4;

    /**
     * After retries are exhausted, publishes the failed record to "<topic>.DLT" instead of
     * silently dropping it (Spring Boot's own default) or endlessly re-delivering it.
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        ExponentialBackOff backOff = new ExponentialBackOff(INITIAL_BACKOFF_INTERVAL_MS, BACKOFF_MULTIPLIER);
        backOff.setMaxAttempts(MAX_ATTEMPTS);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
