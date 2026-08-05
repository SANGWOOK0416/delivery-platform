package com.portfolio.notification.service;

import com.portfolio.common.event.DeliveryRiskEvent;

/**
 * Sends the delivery alert for an order. {@link KakaoMessageService} is the real implementation
 * (Kakao Talk memo API); {@link StubKakaoMessageSender} stands in for it under the "loadtest"
 * profile so load tests never call the real external API.
 */
public interface KakaoMessageSender {

    void sendDeliveryAlert(DeliveryRiskEvent event);
}
