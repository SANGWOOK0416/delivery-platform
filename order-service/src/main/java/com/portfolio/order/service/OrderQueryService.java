package com.portfolio.order.service;

import com.portfolio.order.dto.OrderResponse;
import com.portfolio.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OrderResponse> findRecentOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
