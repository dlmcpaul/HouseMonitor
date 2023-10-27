package com.hz.housemonitor.services;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.Sensor;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.render.*;
import com.hz.housemonitor.models.weather.CurrentWeather;
import com.hz.housemonitor.models.weather.Daily;
import com.hz.housemonitor.models.weather.ForecastWeather;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ViewService {
    private final LocalDBService localDBService;
    private final WeatherService weatherService;

    public List<DayForecast> getForcastWeatherData() {
        ForecastWeather forecast = weatherService.fetchForecast();
        if (forecast.getDaily() != null) {
            return forecast.getDaily().stream()
                    .map(f -> mapToDayForcast(f, forecast.getTimezone_offset()))
                    .toList()
                    .subList(0, 6);
        }
        log.error("Could not fetch Forecast {}", forecast);
        return new ArrayList<>();
    }

    private DayForecast mapToDayForcast(Daily d, int offset) {
        DayForecast singleDay = new DayForecast();
        singleDay.setMin(Math.toIntExact(Math.round(d.getTemp().getMin())));
        singleDay.setMax(Math.toIntExact(Math.round(d.getTemp().getMax())));
        singleDay.setHumidity(d.getHumidity());
        singleDay.setIcon(weatherService.getWeatherIcon(d.getWeather()));
        Date date = new Date((d.getDt() + offset) * 1000);
        ZonedDateTime z = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        singleDay.setName(z.getDayOfWeek().name().substring(0,3));
        return singleDay;
    }

    public SensorCard getCurrentWeatherSensor() {
        CurrentWeather current = weatherService.fetchCurrent();
        ForecastWeather forecast = weatherService.fetchForecast();

        SensorCard sensorCard = new SensorCard();
        sensorCard.setWeatherSensor(true);

        sensorCard.setId((long) current.getSys().getId());
        sensorCard.setTitle(current.getName());
        sensorCard.setIcon(weatherService.getWeatherIcon(current.getWeather()));
        sensorCard.getAttributes().add(new SensorCardAttributeLine("temperature", String.valueOf(current.getMain().getTemp()), "C"));

        if (forecast.getDaily() != null && forecast.getDaily().isEmpty() == false) {
            DayForecast today = mapToDayForcast(forecast.getDaily().get(0), forecast.getTimezone_offset());
            sensorCard.getAttributes().add(new SensorCardAttributeLine("low", String.valueOf(today.getMin()), "C"));
            sensorCard.getAttributes().add(new SensorCardAttributeLine("high", String.valueOf(today.getMax()), "C"));
        } else {
            sensorCard.getAttributes().add(new SensorCardAttributeLine("low", String.valueOf(current.getMain().getTemp_min()), "C"));
            sensorCard.getAttributes().add(new SensorCardAttributeLine("high", String.valueOf(current.getMain().getTemp_max()), "C"));
        }

        return sensorCard;
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }

    public SensorHeader createSensorHeader(long id, String type) {
        Sensor sensor = localDBService.getSensor(id);

        return new SensorHeader(sensor.getId(), sensor.getLabel(), "", type);
    }

    public List<GraphData> getLast24hrsOutsideMeasurements(String type) {
        Optional<Sensor> envSensor = localDBService.getSensors().stream().filter(Sensor::isWeatherSensor).findFirst();
        if (envSensor.isPresent()) {
            return getLast24HrsMeasurements(envSensor.get().getId(), type);
        }
        return new ArrayList<>();
    }

    public List<GraphData> getMeasurementsFor(long id, String type, LocalDate fromDate, LocalDate toDate) {

        List<SensorEvent> sensorEvents;

        if (toDate.isEqual(LocalDate.now())) {
            if (fromDate.isEqual(LocalDate.now())) {
                sensorEvents = localDBService.getEventsForToday(id);
            } else {
                sensorEvents = localDBService.getEventsBetweenTimePeriods(id, fromDate.atStartOfDay(), LocalDateTime.now());
            }
        } else {
            sensorEvents = localDBService.getEventsBetweenDates(id, fromDate, toDate);
        }
        return sensorEvents.stream()
                .filter(sensorEvent -> sensorEvent.getMeasurementList().isEmpty() == false)
                .map(s -> new GraphData(s.getWhen(), getSingleMeasure(s.getMeasurementList(), type).orElseGet(Measurement::new)))
                .toList();
    }

    public List<GraphData> getLast24HrsMeasurements(long id, String type) {
        return getMeasurementsFor(id, type, LocalDate.now(), LocalDate.now());
    }

    public List<GraphData> getTodaysMeasurements(long id, String type) {
        List<SensorEvent> sensorEvents = localDBService.getEventsForToday(id);
        return sensorEvents.stream()
                .filter(sensorEvent -> sensorEvent.getMeasurementList().isEmpty() == false)
                .map(s -> new GraphData(s.getWhen(), getSingleMeasure(s.getMeasurementList(), type).orElseGet(Measurement::new)))
                .toList();
    }

    public SummaryStatistics summariseStats() {
        log.info("summariseStats started at {}", this::now);
        try {
            LocalDateTime from = this.now().minusDays(1);
            SummaryStatistics stats = new SummaryStatistics();
            localDBService.getSensors()
                    .forEach(sensor -> calculateSensorStats(sensor, stats, from));
            return stats;
        } finally {
            log.info("summariseStats completed at {}", this::now);
        }
    }

    private void calculateSensorStats(Sensor sensor, SummaryStatistics summaryStatistics, LocalDateTime from) {
        if (sensor.isEnvironmentSensor()) {
            summaryStatistics.calculateMin(localDBService.getMinTemperatureForSensorAfter(sensor.getId(), from));
            summaryStatistics.calculateMax(localDBService.getMaxTemperatureForSensorAfter(sensor.getId(), from));
        }
    }

    private Optional<Measurement> getSingleMeasure(List<Measurement> measurements, String type) {
        return measurements.stream()
                .filter(m -> m.getType().equalsIgnoreCase(type))
                .distinct()
                .findFirst();
    }

    public List<SensorCard> generateSensorCards() {
        log.info("generateSensorCards started at {}", this::now);
        try {
            List<Sensor> sensors = localDBService.getSensors();
            return sensors.stream()
                    .map(s -> this.createSensorCard(s.getId()))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(SensorCard::getTitle))
                    .toList();
        } finally {
            log.info("generateSensorCards completed at {}", this::now);
        }
    }

    @Cacheable("attributes")
    public List<Filter> getDistinctAttributes(long sensorId) {
        log.info("getDistinctAttributes({}) started at {}", sensorId, LocalDateTime.now());
        try {
            List<Filter> result = new ArrayList<>();
            List<SensorEvent> events = localDBService.getEventsForToday();
            events.stream()
                    .filter(sensorEvent -> sensorEvent.getSensor().getId() == sensorId)
                    .findFirst()
                    .ifPresent(sensorEvent -> result.addAll(sensorEvent.getMeasurementList().stream()
                            .map(measurement -> new Filter(measurement.getType().substring(0, 1).toUpperCase() + measurement.getType().substring(1))).sorted().toList()));
            return result;
        } finally {
            log.info("getDistinctAttributes({}) completed at {}", sensorId, LocalDateTime.now());
        }
    }

    public List<Filter> getDistinctAttributes() {
        log.info("getDistinctAttributes started at {}", this::now);
        try {
            List<Filter> result = new ArrayList<>();
            result.add(new Filter("Humidity"));
            result.add(new Filter("Pressure"));
            result.add(new Filter("Temperature"));
            return result;
        } finally {
            log.info("getDistinctAttributes completed at {}", this::now);
        }
    }

    public SensorCard createSensorCard(long sensorId) {
        List<SensorEvent> events = localDBService.getEventsForToday();

        Optional<SensorEvent> latest = events.stream()
                .filter(sensorEvent -> sensorEvent.getSensor().getId() == sensorId)
                .min((i1, i2) -> i2.getWhen().compareTo(i1.getWhen()));

        if (latest.isPresent()) {

            List<SensorCardAttributeLine> attributes = latest.get().getMeasurementList()
                    .stream()
                    .sorted(Comparator.comparing(Measurement::getType))
                    .filter(this::clean)
                    .map(measurement -> new SensorCardAttributeLine(measurement.getType(), measurement.getValue(), measurement.getUnit()))
                    .toList();

            return createSensorCard(latest.get().getSensor(), latest.get().getMeasurementList(), attributes);
        }

        return null;
    }

    private SensorCard createSensorCard(Sensor sensor, List<Measurement> measurements, List<SensorCardAttributeLine> attributes) {
        String label = sensor.getLabel();
        String icon = "fas " + getRoomIcon(label);

        if (sensor.isWeatherSensor()) {
            label = measurements.stream()
                    .filter(measurement -> measurement.getType().equalsIgnoreCase("city"))
                    .distinct().findFirst().orElse(new Measurement(label)).getValue();
            icon = selectIcon(getWeatherHintFromMeasurements(measurements));
        }

        return new SensorCard(sensor.getId(), label, icon, attributes, sensor.isWeatherSensor());
    }

    private String getRoomIcon(String roomName) {
        if (roomName.toLowerCase().contains("bed")) {
            return "fa-bed";
        } else if (roomName.toLowerCase().contains("bath")) {
            return "fa-bath";
        } else if (roomName.toLowerCase().contains("kitchen")) {
            return "fa-sink";
        } else if (roomName.toLowerCase().contains("tv")) {
            return "fa-tv";
        }
        return "fa-home";
    }

    private String getWeatherHintFromMeasurements(List<Measurement> measurements) {
        return getSingleMeasure(measurements, "weathericons").orElseThrow().getValue();
    }

    private String selectIcon(String hint) {
        return "fas fa-cloud-sun";
    }

    private boolean clean(Measurement measurement) {
        return measurement.getType().equalsIgnoreCase("battery")
                || measurement.getType().equalsIgnoreCase("humidity")
                || measurement.getType().equalsIgnoreCase("presence")
                || measurement.getType().equalsIgnoreCase("voltage")
                || measurement.getType().equalsIgnoreCase("temperature")
                || measurement.getType().equalsIgnoreCase("pressure")
                || measurement.getType().equalsIgnoreCase("voc")
                || measurement.getType().equalsIgnoreCase("pm25")
                || measurement.getType().equalsIgnoreCase("formaldehyde")
                || measurement.getType().equalsIgnoreCase("carbondioxide")
                || measurement.getType().equalsIgnoreCase("airqualityindex")
                || measurement.getType().equalsIgnoreCase("winddirection")
                || measurement.getType().equalsIgnoreCase("windspeed")
                || measurement.getType().equalsIgnoreCase("weather");
    }
}
