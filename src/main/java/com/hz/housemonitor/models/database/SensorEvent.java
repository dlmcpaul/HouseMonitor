package com.hz.housemonitor.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(indexes = {  @Index(name = "idx_Sensor_Event_A", columnList = "sensor_id, when"),
                    @Index(name = "idx_Sensor_Event_B", columnList = "sensor_event_id, when"),
                    @Index(name = "idx_Sensor_Event_C", columnList = "sensor_id"),
                    @Index(name = "idx_Sensor_Event_D", columnList = "when")})
@RequiredArgsConstructor
public class SensorEvent {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name="sensor_event_id")
    private Long id;
    @Column(name="WHEN")
    private LocalDateTime when;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sensor_id")
    private Sensor sensor;

    @OneToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="sensor_event_id")
    @ToString.Exclude
    private List<Measurement> measurementList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SensorEvent that = (SensorEvent) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 832663928;
    }

    @Override
    public String toString() {
        return "SensorEvent::" + this.id;
    }
}
