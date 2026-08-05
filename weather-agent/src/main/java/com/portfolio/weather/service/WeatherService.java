package com.portfolio.weather.service;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.client.GeoCoordinate;
import com.portfolio.weather.client.GeocodingClient;
import com.portfolio.weather.client.GeocodingException;
import com.portfolio.weather.client.KmaGridConverter;
import com.portfolio.weather.client.WeatherApiClient;
import com.portfolio.weather.config.KmaApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final GeocodingClient geocodingClient;
    private final KmaGridConverter kmaGridConverter;
    private final WeatherApiClient weatherApiClient;
    private final KmaApiProperties kmaApiProperties;

    public DeliveryRiskEvent analyzeDeliveryRisk(OrderCreatedEvent orderEvent) {
        KmaApiProperties.Grid grid = resolveGrid(orderEvent.address());
        int precipitationType = weatherApiClient.fetchCurrentPrecipitationType(grid.nx(), grid.ny());
        return new DeliveryRiskEvent(
                orderEvent.orderId(),
                orderEvent.address(),
                precipitationType
        );
    }

    private KmaApiProperties.Grid resolveGrid(String address) {
        try {
            GeoCoordinate coordinate = geocodingClient.geocode(address);
            KmaApiProperties.Grid grid = kmaGridConverter.toGrid(coordinate.latitude(), coordinate.longitude());
            log.info("Resolved delivery address to a KMA grid cell. address={}, nx={}, ny={}",
                    address, grid.nx(), grid.ny());
            return grid;
        } catch (GeocodingException exception) {
            log.warn("Failed to geocode delivery address; falling back to default grid coordinate. address={}, reason={}",
                    address, exception.getMessage());
            return kmaApiProperties.grid();
        }
    }
}
