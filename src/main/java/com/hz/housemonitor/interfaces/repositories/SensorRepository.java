package com.hz.housemonitor.interfaces.repositories;

import com.hz.housemonitor.models.database.Sensor;
import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<Sensor, Long> {
}
