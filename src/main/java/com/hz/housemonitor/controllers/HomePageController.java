package com.hz.housemonitor.controllers;

import com.hz.housemonitor.configuration.ReleaseInfoContributor;
import com.hz.housemonitor.models.render.SensorCard;
import com.hz.housemonitor.services.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class HomePageController {
    private final ViewService viewService;
    private final ReleaseInfoContributor release;

    // Generate main page from template index.html
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<SensorCard> sensorCards = viewService.generateSensorCards();

            model.addAttribute("selectedFilter", "Temperature");
            model.addAttribute("TZ", Calendar.getInstance().getTimeZone().toZoneId().getId());
            model.addAttribute("releaseVersion", release.getVersion());
            model.addAttribute("availableFilters", viewService.getDistinctAttributes());
            model.addAttribute("currentDateTime", LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS));
            model.addAttribute("summary", viewService.summariseStats());
            model.addAttribute("sensorCards", sensorCards.stream().filter(card -> card.isWeatherSensor() == false).collect(Collectors.toList()));
            model.addAttribute("forecast", viewService.getForcastWeatherData());
            model.addAttribute("currentWeather", viewService.getCurrentWeatherSensor());

        } catch (Exception e) {
            log.error("index Page Exception {} {}", e.getMessage(), e);
        }
        return "index";
    }
}
