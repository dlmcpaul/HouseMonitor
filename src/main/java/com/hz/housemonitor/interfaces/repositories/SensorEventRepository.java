package com.hz.housemonitor.interfaces.repositories;

import com.hz.housemonitor.models.database.SensorEvent;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorEventRepository extends CrudRepository<SensorEvent, Long> {
    List<SensorEvent> findSensorEventByWhenBetweenOrderByWhen(LocalDateTime from, LocalDateTime before);
}
