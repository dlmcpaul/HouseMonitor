package com.hz.housemonitor.models.weather;

import lombok.Data;

@Data
public class Sys {
    private float type;
    private float id;
    private String country;
    private float sunrise;
    private float sunset;
}
