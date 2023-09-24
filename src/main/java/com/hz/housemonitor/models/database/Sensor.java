package com.hz.housemonitor.models.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Sensor {
    @Id
    @Column(name="sensor_id")
    private Long id;
    private String label;
    private String type;

    public boolean isWeatherSensor() {
        return type.equalsIgnoreCase("weather");
    }

    public boolean isEnvironmentSensor() {
        return type.equalsIgnoreCase("environment");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Sensor sensor = (Sensor) o;

        return Objects.equals(id, sensor.id);
    }

    @Override
    public int hashCode() {
        return 632374272;
    }
}
