package com.hz.housemonitor.models.render;

import lombok.Data;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@Data
public class FileDownload {
	private String status = "Export";
	private LocalDate created;
	private InputStreamResource outputFile;
	private int percentComplete;
	private String failureReason = "";

	public FileDownload(LocalDate created) {
		this.created = created;
	}

	public boolean isToday() {
		return this.created.isEqual(LocalDate.now());
	}

	public boolean isNew() {
		return status.equalsIgnoreCase("export");
	}

	public boolean isExportRunning() {
		return status.equalsIgnoreCase("started") || status.equalsIgnoreCase("processing");
	}

	public boolean isFileReady() {
		return status.equalsIgnoreCase("download");
	}

	public void started() {
		this.status = "Started";
	}

	public void processing(int percentComplete) {
		this.status = "Processing";
		this.percentComplete = percentComplete;
	}

	public void completed(ByteArrayInputStream bytes) {
		outputFile = new InputStreamResource(bytes);
		this.status = "Download";
	}

	public void failed(String reason) {
		this.status = "Failed";
		this.failureReason = reason;
	}

}
