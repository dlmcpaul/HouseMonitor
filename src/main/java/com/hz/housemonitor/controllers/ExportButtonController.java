package com.hz.housemonitor.controllers;

import com.hz.housemonitor.models.render.FileDownload;
import com.hz.housemonitor.services.DataExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ExportButtonController {
	private final DataExportService dataExportService;

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

}
