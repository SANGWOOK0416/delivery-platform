package com.portfolio.order.service;

import com.portfolio.order.dto.OrderResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Fans out newly created orders to connected dashboard clients over SSE. Broadcast happens
 * in-process right after the order is saved — no Kafka round trip needed, since order-service
 * already has this information the moment it exists.
 */
@Slf4j
@Component
public class OrderEventBroadcaster {

    private static final String EVENT_NAME = "order-created";

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(exception -> emitters.remove(emitter));
        return emitter;
    }

    public void broadcastNewOrder(OrderResponse order) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(EVENT_NAME).data(order));
            } catch (IOException exception) {
                emitter.completeWithError(exception);
                emitters.remove(emitter);
            }
        }
    }
}
