package com.portfolio.notification.service;

import com.portfolio.notification.dto.NotificationStatusResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Fans out delivery-attempt outcomes to connected dashboard clients over SSE. Broadcast happens
 * in-process right after the outcome is recorded — no new Kafka topic needed, since
 * notification-service already has this information the moment it happens.
 */
@Component
public class NotificationEventBroadcaster {

    private static final String EVENT_NAME = "notification-status-changed";

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(exception -> emitters.remove(emitter));
        return emitter;
    }

    public void broadcastStatusChange(NotificationStatusResponse status) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(EVENT_NAME).data(status));
            } catch (IOException exception) {
                emitter.completeWithError(exception);
                emitters.remove(emitter);
            }
        }
    }
}
