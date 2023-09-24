package com.hz.housemonitor.services;

import com.hz.housemonitor.interfaces.repositories.SensorEventRepository;
import com.hz.housemonitor.interfaces.repositories.SensorRepository;
import com.hz.housemonitor.models.database.Sensor;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.hubitat.Device;
import com.hz.housemonitor.models.hubitat.Devices;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class SensorEventManager {
    private final SensorSourceService sensorSourceService;
    private final SensorRepository sensorRepository;
    private final SensorEventRepository sensorEventRepository;
    private final TransformService transformService;
    private final WeatherService weatherService;

    @Scheduled(cron = "${housemonitor.cron-schedule}")
    @Transactional
    public void gather() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
        log.info("Gather Started at {}", now);
        try {
            Devices devices = sensorSourceService.fetchData();
            List<SensorEvent> sensorEvents = devices.getDevicesList().stream()
                    .map(device -> storeSensor(device))
                    .map(device -> makeSensorEvent(sensorRepository.findById(Long.valueOf(device.getId())), now))
                    .toList();
            sensorEvents.stream().forEach(sensorEvent -> transformService.mapMeasurements(sensorEvent, findMatchingDevice(devices, sensorEvent.getSensor().getId())));
            sensorEventRepository.saveAll(sensorEvents);
            weatherService.cacheClean();
        } finally {
            log.info("Gather Completed at {}", LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS));
        }
    }

    private Device findMatchingDevice(Devices devices, Long id) {
        Optional<Device> result = devices.getDevicesList().stream()
                .filter(device -> Long.valueOf(device.getId()).equals(id))
                .findFirst();

        return result.orElseThrow();
    }

    private SensorEvent makeSensorEvent(Optional<Sensor> sensor, LocalDateTime now) {
        SensorEvent sensorEvent = new SensorEvent();
        sensorEvent.setSensor(sensor.orElse(null));
        sensorEvent.setWhen(now);
//        sensorEventRepository.save(sensorEvent);
        return sensorEvent;
    }

    private Device storeSensor(Device device) {
        Sensor sensor = new Sensor();
        sensor.setId(Long.valueOf(device.getId()));
        sensor.setLabel(device.getLabel());
        if (device.getType().contains("Temp")) {
            sensor.setType("Environment");
        } else if (device.getType().contains("Weather")) {
            sensor.setType("Weather");
        } else {
            sensor.setType(device.getType());
        }
        sensorRepository.save(sensor);
        return device;
    }
}
