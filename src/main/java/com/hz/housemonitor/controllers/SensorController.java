package com.hz.housemonitor.controllers;

import com.hz.housemonitor.configuration.ReleaseInfoContributor;
import com.hz.housemonitor.models.render.SensorCard;
import com.hz.housemonitor.services.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class SensorController {
    private final ViewService viewService;
    private final ReleaseInfoContributor release;

    @GetMapping("/sensors")
    public String sensors(Model model, @RequestParam(value = "filter", defaultValue = "Temperature") String filter) {
        try {
            List<SensorCard> sensorCards = viewService.generateSensorCards();

            model.addAttribute("TZ", Calendar.getInstance().getTimeZone().toZoneId().getId());
            model.addAttribute("releaseVersion", release.getVersion());
            model.addAttribute("availableFilters", viewService.getDistinctAttributes());
            model.addAttribute("selectedFilter", filter);
            model.addAttribute("sensorCards", sensorCards.stream().filter(card -> card.isWeatherSensor() == false).toList());
        } catch (Exception e) {
            log.error("Sensors Page Exception {} {}", e.getMessage(), e);
        }
        return "fragments/Cards :: sensorCards (filters = ${availableFilters}, cards = ${sensorCards}, selected = ${selectedFilter})";
    }
}
