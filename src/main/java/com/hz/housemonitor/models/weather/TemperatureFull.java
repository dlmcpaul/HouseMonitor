package com.hz.housemonitor.models.weather;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TemperatureFull extends Temperature {
    private double min;
    private double max;
}
