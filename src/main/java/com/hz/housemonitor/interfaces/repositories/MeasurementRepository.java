package com.hz.housemonitor.interfaces.repositories;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.projection.HighLowStatistic;
import com.hz.housemonitor.models.database.projection.TemperatureStat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {
    @Query(value = """
            select s.label, MIN(m.value) as min, MAX(m.value) as max from measurement as m, sensor_event as e, sensor as s
            where e.sensor_event_id = m.sensor_event_id
            and s.sensor_id = e.sensor_id
            and s.sensor_id = ?1
            and e.when >= ?2
            and m.type = ?3
            group by s.label""", nativeQuery = true)
    HighLowStatistic getHighLowStat(Long id, LocalDateTime start, String type);

    @Query(value= """
            select top 1 (select s.label from sensor s where s.sensor_id = ?1) as label, e.when, m.value as temperature
            from sensor_event as e, measurement as m
            where e.sensor_id = ?1
            and e.when >= ?2
            and m.type = 'temperature'
            and m.sensor_event_id = e.sensor_event_id
            order by m.value;
            """, nativeQuery = true)
    TemperatureStat getMinTemperatureFor(Long id, LocalDateTime start);

    @Query(value= """
            select top 1 (select s.label from sensor s where s.sensor_id = ?1) as label, e.when, m.value as temperature
            from sensor_event as e, measurement as m
            where e.sensor_id = ?1
            and e.when >= ?2
            and m.type = 'temperature'
            and m.sensor_event_id = e.sensor_event_id
            order by m.value desc;
            """, nativeQuery = true)
    TemperatureStat getMaxTemperatureFor(Long id, LocalDateTime start);
}
