package com.hz.housemonitor.models.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather {
    private Coordinates coord;
    private List<Weather> weather = new ArrayList<> ();
    private String base;
    private Main main;
    private float visibility;
    private Wind wind;
    private Clouds clouds;
    private float dt;
    private Sys sys;
    private float timezone;
    private float id;
    private String name;
    private float cod;
}
