package com.hz.housemonitor.models.render;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DayForecast {
    private String name;
    private String icon;
    private int min;
    private int max;
    private int humidity;
}
