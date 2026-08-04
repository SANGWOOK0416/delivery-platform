package com.portfolio.common.event;

public final class KafkaTopics {

    public static final String ORDER_EVENTS = "order-events";
    public static final String DELIVERY_RISK_EVENTS = "delivery-risk-events";

    // Spring Kafka's DeadLetterPublishingRecoverer default naming convention (<topic>.DLT).
    public static final String ORDER_EVENTS_DLT = ORDER_EVENTS + ".DLT";
    public static final String DELIVERY_RISK_EVENTS_DLT = DELIVERY_RISK_EVENTS + ".DLT";

    private KafkaTopics() {
    }
}
