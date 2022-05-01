package com.hz.housemonitor.models.weather;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Current extends WeatherPoint {
    private long sunrise;
    private long sunset;
    private Rain rain;
}
