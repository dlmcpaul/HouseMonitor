package com.hz.housemonitor.interfaces.repositories;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.projection.HighLowStatistic;
import com.hz.housemonitor.models.database.projection.TemperatureStat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {
    @Query(value = "select s.label, MIN(m.value) as min, MAX(m.value) as max from measurement as m, sensor_event as e, sensor as s\n" +
            "where e.sensor_event_id = m.sensor_event_id\n" +
            "and s.sensor_id = e.sensor_id\n" +
            "and s.sensor_id = ?1\n" +
            "and e.when >= ?2\n" +
            "and m.type = ?3\n" +
            "group by s.label", nativeQuery = true)
    HighLowStatistic getHighLowStat(Long id, LocalDateTime start, String type);

    @Query(value = "select top 1 s.label, e.when, m.value as temperature\n" +
            "from measurement as m, sensor_event as e, sensor as s\n" +
            "where e.sensor_event_id = m.sensor_event_id\n" +
            "and s.sensor_id = e.sensor_id\n" +
            "and m.type = 'temperature'\n" +
            "and s.sensor_id = ?1\n" +
            "and e.when >= ?2\n" +
            "and m.value =  (select min(m.value)\n" +
            "from measurement as m, sensor_event as e, sensor as s\n" +
            "where e.sensor_event_id = m.sensor_event_id\n" +
            "and s.sensor_id = e.sensor_id\n" +
            "and m.type = 'temperature'\n" +
            "and s.sensor_id = ?1\n" +
            "and e.when >= ?2) \n" +
            "order by e.when desc", nativeQuery = true)
    TemperatureStat getMinTemperatureFor(Long id, LocalDateTime start);

    @Query(value = "select top 1 s.label, e.when, m.value as temperature\n" +
            "from measurement as m, sensor_event as e, sensor as s\n" +
            "where e.sensor_event_id = m.sensor_event_id\n" +
            "and s.sensor_id = e.sensor_id\n" +
            "and m.type = 'temperature'\n" +
            "and s.sensor_id = ?1\n" +
            "and e.when >= ?2\n" +
            "and m.value =  (select max(m.value)\n" +
            "from measurement as m, sensor_event as e, sensor as s\n" +
            "where e.sensor_event_id = m.sensor_event_id\n" +
            "and s.sensor_id = e.sensor_id\n" +
            "and m.type = 'temperature'\n" +
            "and s.sensor_id = ?1\n" +
            "and e.when >= ?2) \n" +
            "order by e.when desc", nativeQuery = true)
    TemperatureStat getMaxTemperatureFor(Long id, LocalDateTime start);
}
