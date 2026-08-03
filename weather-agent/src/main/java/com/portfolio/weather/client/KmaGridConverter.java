package com.portfolio.weather.client;

import com.portfolio.weather.config.KmaApiProperties;
import org.springframework.stereotype.Component;

/**
 * Converts WGS84 latitude/longitude into the KMA forecast grid cell (nx, ny),
 * using the Lambert Conformal Conic projection published by the Korea
 * Meteorological Administration for its short-term forecast APIs.
 */
@Component
public class KmaGridConverter {

    private static final double EARTH_RADIUS_KM = 6371.00877;
    private static final double GRID_SPACING_KM = 5.0;
    private static final double STANDARD_LATITUDE_1 = 30.0;
    private static final double STANDARD_LATITUDE_2 = 60.0;
    private static final double ORIGIN_LONGITUDE = 126.0;
    private static final double ORIGIN_LATITUDE = 38.0;
    private static final double ORIGIN_GRID_X = 43;
    private static final double ORIGIN_GRID_Y = 136;
    private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    public KmaApiProperties.Grid toGrid(double latitude, double longitude) {
        double re = EARTH_RADIUS_KM / GRID_SPACING_KM;
        double slat1 = STANDARD_LATITUDE_1 * DEGREES_TO_RADIANS;
        double slat2 = STANDARD_LATITUDE_2 * DEGREES_TO_RADIANS;
        double olon = ORIGIN_LONGITUDE * DEGREES_TO_RADIANS;
        double olat = ORIGIN_LATITUDE * DEGREES_TO_RADIANS;

        double sn = Math.log(Math.cos(slat1) / Math.cos(slat2))
                / Math.log(Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5));
        double sf = Math.pow(Math.tan(Math.PI * 0.25 + slat1 * 0.5), sn) * Math.cos(slat1) / sn;
        double ro = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + olat * 0.5), sn);

        double ra = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + latitude * DEGREES_TO_RADIANS * 0.5), sn);
        double theta = normalizeTheta(longitude * DEGREES_TO_RADIANS - olon) * sn;

        int nx = (int) Math.floor(ra * Math.sin(theta) + ORIGIN_GRID_X + 0.5);
        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + ORIGIN_GRID_Y + 0.5);

        return new KmaApiProperties.Grid(nx, ny);
    }

    private double normalizeTheta(double theta) {
        if (theta > Math.PI) {
            return theta - 2.0 * Math.PI;
        }
        if (theta < -Math.PI) {
            return theta + 2.0 * Math.PI;
        }
        return theta;
    }
}
