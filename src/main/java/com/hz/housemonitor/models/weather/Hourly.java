package com.hz.housemonitor.models.weather;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Hourly extends WeatherPoint {
    private double wind_gust;
    private int pop;
}
