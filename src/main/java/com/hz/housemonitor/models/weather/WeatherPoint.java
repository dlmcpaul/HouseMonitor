package com.hz.housemonitor.models.weather;

import lombok.Data;

@Data
public class WeatherPoint {
    private long dt;
    private double temp;
    private double feels_like;
    private int pressure;
    private int humidity;
    private double dew_point;
    private double uvi;
    private int clouds;
    private int visibility;
    private int wind_speed;
    private int wind_deg;
}
