package com.hz.housemonitor.controllers;

import com.hz.housemonitor.configuration.ReleaseInfoContributor;
import com.hz.housemonitor.services.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;

@Controller
@RequiredArgsConstructor
@Log4j2
public class SensorHistoryController {
    private final ViewService viewService;
    private final ReleaseInfoContributor release;

    @GetMapping("/sensor/{id}")
    public String getSensorData(@PathVariable("id") long id, @RequestParam(value = "filter", defaultValue = "Temperature") String filter, Model model) {

        updateCommonModelAttributes(model, id, filter);
        return "history";
    }

    @GetMapping("/sensor/{id}/graph")
    public String getGraphData(@PathVariable("id") long id, @RequestParam(value = "filter", defaultValue = "Temperature") String filter, Model model) {

        updateCommonModelAttributes(model, id, filter);
        return "fragments/Graphs :: historyGraph (filters = ${availableFilters}, selected = ${selectedFilter}, id=${id})";
    }

    private void updateCommonModelAttributes(Model model, long id, String filter) {
        try {
            model.addAttribute("TZ", Calendar.getInstance().getTimeZone().toZoneId().getId());
            model.addAttribute("releaseVersion", release.getVersion());
            model.addAttribute("sensorData", viewService.getLast24HrsMeasurements(id, filter.toLowerCase()));
            model.addAttribute("referenceData", viewService.getLast24hrsOutsideMeasurements(filter.toLowerCase()));
            model.addAttribute("sensor", viewService.createSensorCard(id));
            model.addAttribute("availableFilters", viewService.getDistinctAttributes());
            model.addAttribute("selectedFilter", filter);
            model.addAttribute("id", id);
        } catch (Exception e) {
            log.error("Sensor History Page Exception {} {}", e.getMessage(), e);
        }
    }
}
