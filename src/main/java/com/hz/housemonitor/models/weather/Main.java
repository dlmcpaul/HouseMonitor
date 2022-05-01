package com.hz.housemonitor.models.weather;

import lombok.Data;

@Data
public class Main {
    private long temp;
    private long feels_like;
    private long temp_min;
    private long temp_max;
    private int pressure;
    private int humidity;
}
