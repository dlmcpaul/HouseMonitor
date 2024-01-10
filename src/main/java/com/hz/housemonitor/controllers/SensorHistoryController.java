package com.hz.housemonitor.controllers;

import com.hz.housemonitor.configuration.ReleaseInfoContributor;
import com.hz.housemonitor.models.render.FileDownload;
import com.hz.housemonitor.services.DataExportService;
import com.hz.housemonitor.services.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Calendar;

@Controller
@RequiredArgsConstructor
@Log4j2
public class SensorHistoryController {
    private final ViewService viewService;
    private final DataExportService dataExportService;
    private final ReleaseInfoContributor release;

    @GetMapping("/sensor/{id}")
    public String getSensorData(@PathVariable("id") long id, @RequestParam(value = "filter", defaultValue = "Temperature") String filter, Model model) {

        updateCommonModelAttributes(model, id, filter, "", LocalDate.now().minusDays(1), LocalDate.now());
        return "history";
    }

    @GetMapping("/sensor/{id}/graph")
    public String getGraphData(@PathVariable("id") long id, @RequestParam(value = "filter", defaultValue = "Temperature") String filter, @RequestParam(value="compareTo", defaultValue="") String compareTo, @RequestParam(value="fromDate", defaultValue="") LocalDate fromDate, @RequestParam(value="toDate", defaultValue="") LocalDate toDate, Model model) {

        updateCommonModelAttributes(model, id, filter, compareTo, fromDate, toDate);
        return "fragments/Graphs :: historyGraph (filters = ${availableFilters}, selected = ${selectedFilter}, id=${id})";
    }

    @PostMapping("/sensor/{id}/export")
    public String exportData(@PathVariable("id") long id, Model model) {

        FileDownload fileDownload = dataExportService.getExportResult(id);
        fileDownload.started();

        new Thread(() -> dataExportService.generateCSV(id, fileDownload)).start();

        model.addAttribute("id", id);
        model.addAttribute("file", fileDownload);
        return "fragments/ExportComponent :: exportButton ( file = ${file} )";
    }

    @GetMapping("/sensor/{id}/export")
    public String pollExport(@PathVariable("id") long id, Model model) {

        model.addAttribute("id", id);
        model.addAttribute("file", dataExportService.getExportResult(id));
        return "fragments/ExportComponent :: exportButton ( file = ${file} )";
    }

    @GetMapping("/sensor/{id}/download")
    public ResponseEntity<Resource> getExportData(@PathVariable("id") long id) {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(dataExportService.getExportResult(id).getOutputFile());
    }

    private void updateCommonModelAttributes(Model model, long id, String filter, String compareTo, LocalDate fromDate, LocalDate toDate) {
        try {
            model.addAttribute("TZ", Calendar.getInstance().getTimeZone().toZoneId().getId());
            model.addAttribute("releaseVersion", release.getVersion());
            model.addAttribute("sensorData", viewService.getLast24HrsMeasurements(id, filter.toLowerCase()));
            model.addAttribute("referenceData", viewService.getLast24hrsOutsideMeasurements(filter.toLowerCase()));
            model.addAttribute("comparisonData", null);
            model.addAttribute("sensor", viewService.createSensorCard(id));
            model.addAttribute("availableFilters", viewService.getDistinctAttributes(id));
            model.addAttribute("selectedFilter", filter);
            model.addAttribute("id", id);
            model.addAttribute("file", dataExportService.getExportResult(id));
        } catch (Exception e) {
            log.error("Sensor History Page Exception {} {}", e.getMessage(), e);
        }
    }
}
