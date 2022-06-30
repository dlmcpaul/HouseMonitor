package com.hz.housemonitor.services;

import com.hz.housemonitor.interfaces.repositories.MeasurementRepository;
import com.hz.housemonitor.interfaces.repositories.SensorEventRepository;
import com.hz.housemonitor.interfaces.repositories.SensorRepository;
import com.hz.housemonitor.models.database.Sensor;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.database.projection.HighLowStatistic;
import com.hz.housemonitor.models.database.projection.TemperatureStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class LocalDBService {
    private final SensorRepository sensorRepository;
    private final SensorEventRepository sensorEventRepository;
    private final MeasurementRepository measurementRepository;

    public long countSensors() {
        return sensorRepository.count();
    }

    public Sensor getSensor(long id) {
        return sensorRepository.findById(id).orElseThrow();
    }
    public List<Sensor> getSensors() {
        return StreamSupport.stream(sensorRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public TemperatureStat getMinTemperatureForSensorAfter(Long sensorId, LocalDateTime from) {
        return measurementRepository.getMinTemperatureFor(sensorId, from);
    }

    public TemperatureStat getMaxTemperatureForSensorAfter(Long sensorId, LocalDateTime from) {
        return measurementRepository.getMaxTemperatureFor(sensorId, from);
    }

    public HighLowStatistic getMeasurementsStatsFor(Long sensorId, LocalDateTime from, String type) {
        return measurementRepository.getHighLowStat(sensorId, from, type);
    }

    public List<SensorEvent> getEventsFor(LocalDate date) {
        return sensorEventRepository.findSensorEventByWhenBetweenOrderByWhen(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    public List<SensorEvent> getEventsBetween(LocalDate from, LocalDate before) {
        return sensorEventRepository.findSensorEventByWhenBetweenOrderByWhen(from.atStartOfDay(), before.plusDays(1).atStartOfDay());
    }

    public List<SensorEvent> getEventsForToday() {
        return getEventsFor(LocalDate.now(ZoneId.systemDefault()));
    }

    public List<SensorEvent> getEventsForToday(long id) {
        return getEventsForToday().stream()
                .filter(sensorEvent -> sensorEvent.getSensor().getId() == id)
                .collect(Collectors.toList());
    }

    public List<SensorEvent> getEventsForPeriod(long id, LocalDateTime from, LocalDateTime before) {
        List<SensorEvent> sensorEvents = sensorEventRepository.findSensorEventByWhenBetweenOrderByWhen(from, before);
        return sensorEvents.stream()
                .filter(sensorEvent -> sensorEvent.getSensor().getId() == id)
                .collect(Collectors.toList());
    }

}
