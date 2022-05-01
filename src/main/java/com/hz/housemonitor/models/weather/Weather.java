package com.hz.housemonitor.models.weather;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Weather {
    private int id;
    private String main;
    private String description;
    private String icon;

    public Weather(String icon, int id) {
        this.icon = icon;
        this.id = id;
    }
}
