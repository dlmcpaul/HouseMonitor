package com.hz.housemonitor.services;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.render.FileDownload;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DataExportService {
	private final LocalDBService localDBService;

	private final Map<Long, FileDownload> exportResults = new HashMap<>();

	public DataExportService(LocalDBService localDBService) {
		this.localDBService = localDBService;
	}

	public FileDownload getExportResult(long sensorId) {

		// Return cached Download date unless expired (> 1 day old)
		if (exportResults.containsKey(sensorId) && exportResults.get(sensorId).isToday()) {
			return exportResults.get(sensorId);
		}

		FileDownload exportResult = new FileDownload(LocalDate.now());
		exportResults.put(sensorId, exportResult);

		return exportResult;
	}

	private String generateRow(List<Measurement> measurements) {
		return measurements.stream().sorted(Comparator.comparing(Measurement::getType)).map(Measurement::getValue).collect(Collectors.joining(","));
	}

	private void writeLine(ByteArrayOutputStream out, SensorEvent line, boolean writeHeader) {
		if (writeHeader) {
			out.writeBytes("when,".getBytes(StandardCharsets.UTF_8));
			out.writeBytes(line.getMeasurementList().stream().sorted(Comparator.comparing(Measurement::getType)).map(Measurement::getType).collect(Collectors.joining(",")).getBytes(StandardCharsets.UTF_8));
			out.write(10);
		}
		out.writeBytes(line.getWhen().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).concat(",").getBytes(StandardCharsets.UTF_8));
		out.writeBytes(generateRow(line.getMeasurementList()).getBytes(StandardCharsets.UTF_8));
		out.write(10);
	}

	@Transactional
	public void generateCSV(long sensorId, FileDownload exportResult) {

		log.info("generateCSV started at {}", this::now);

		try {
			Pageable pageable = Pageable.ofSize(5000);
			int totalRows = localDBService.getCountSensorEvents(sensorId);
			int totalProcessed = 0;
			Slice<SensorEvent> slice = localDBService.getSensorEventsBySlice(sensorId, pageable);
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				boolean hasData = slice.hasContent();
				while (hasData) {
					slice.stream().forEach(sensorEvent -> writeLine(out, sensorEvent, out.size() == 0));
					out.flush();
					totalProcessed += slice.getNumberOfElements();
					exportResult.processing((totalProcessed * 100) / totalRows);
					log.debug("processed {}%", exportResult.getPercentComplete());
					hasData = slice.hasNext();
					if (hasData) {
						pageable = pageable.next();
						slice = localDBService.getSensorEventsBySlice(sensorId, pageable);
					}
				}
				exportResult.completed(new ByteArrayInputStream(out.toByteArray()));
			} catch (IOException e) {
				log.error(e);
				exportResult.failed(e.getMessage());
			}
		} finally {
			log.info("generateCSV completed at {}", this::now);
		}

	}

	private LocalDateTime now() {
		return LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
	}

}
