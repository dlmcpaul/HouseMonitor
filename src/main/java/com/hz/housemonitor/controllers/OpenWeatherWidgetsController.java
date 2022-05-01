package com.hz.housemonitor.controllers;

import com.hz.housemonitor.services.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OpenWeatherWidgetsController {
    private final ViewService viewService;

    @GetMapping("/weather/current")
    public String current(Model model) {
        try {
            model.addAttribute("currentWeather", viewService.getCurrentWeatherSensor());
        } catch (Exception e) {
            log.error("Current Weather Widget Exception {} {}", e.getMessage(), e);
        }
        return "fragments/Weather :: current (sensor=${currentWeather})";
    }

    @GetMapping("/weather/forecast")
    public String forecast(Model model) {
        try {
            model.addAttribute("forecast", viewService.getForcastWeatherData());
        } catch (Exception e) {
            log.error("Forecast Weather Widget Exception {} {}", e.getMessage(), e);
        }
        return "fragments/Weather :: forecast (days=${forecast})";
    }
}
