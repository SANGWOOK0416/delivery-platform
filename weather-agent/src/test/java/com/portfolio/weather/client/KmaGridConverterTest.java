package com.portfolio.weather.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.weather.config.KmaApiProperties;
import org.junit.jupiter.api.Test;

class KmaGridConverterTest {

    private final KmaGridConverter converter = new KmaGridConverter();

    @Test
    void convertsSeoulCityHallToItsKnownReferenceGridCell() {
        KmaApiProperties.Grid grid = converter.toGrid(37.5665, 126.9780);

        assertThat(grid).isEqualTo(new KmaApiProperties.Grid(60, 127));
    }
}
