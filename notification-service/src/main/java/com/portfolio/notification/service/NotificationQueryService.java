package com.portfolio.notification.service;

import com.portfolio.notification.dto.NotificationStatusResponse;
import com.portfolio.notification.repository.NotificationLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationLogRepository notificationLogRepository;

    public List<NotificationStatusResponse> findLatestStatusPerOrder() {
        return notificationLogRepository.findLatestPerOrder().stream()
                .map(NotificationStatusResponse::from)
                .toList();
    }
}
