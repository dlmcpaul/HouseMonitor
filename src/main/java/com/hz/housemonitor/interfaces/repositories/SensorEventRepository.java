package com.hz.housemonitor.interfaces.repositories;

import com.hz.housemonitor.models.database.SensorEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorEventRepository extends JpaRepository<SensorEvent, Long> {
    List<SensorEvent> findSensorEventByWhenBetweenOrderByWhen(LocalDateTime from, LocalDateTime before);

    @Query(value = "select top 1 * from Sensor_Event where sensor_id = ?1 order by WHEN asc", nativeQuery = true)
    SensorEvent findFirstSensorEventForId(long sensorId);

    Slice<SensorEvent> findAllBySensorId(long sensorId, Pageable pageable);

    @Query(value = "select count(*) from Sensor_Event where sensor_id = ?1", nativeQuery = true)
    int countForId(long sensorId);
}
