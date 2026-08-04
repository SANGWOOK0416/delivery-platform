package com.portfolio.notification.controller;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.notification.dto.NotificationStatusResponse;
import com.portfolio.notification.service.NotificationEventBroadcaster;
import com.portfolio.notification.service.NotificationQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationQueryController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationEventBroadcaster notificationEventBroadcaster;

    @GetMapping("/latest")
    public ApiResponse<List<NotificationStatusResponse>> latestStatuses() {
        return ApiResponse.success(notificationQueryService.findLatestStatusPerOrder());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return notificationEventBroadcaster.subscribe();
    }
}
