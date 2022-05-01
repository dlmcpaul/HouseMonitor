package com.hz.housemonitor.models.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(indexes = {  @Index(name = "idx_Sensor_Event_1", columnList = "when"),
                    @Index(name = "idx_Sensor_Event_2", columnList = "sensor_id, when")})
@RequiredArgsConstructor
public class SensorEvent {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "sensor_event_generator")
    @SequenceGenerator(name = "sensor_event_generator", initialValue = 900000)
    @Column(name="sensor_event_id")
    private Long id;
    private LocalDateTime when;

    @OneToOne(fetch = FetchType.LAZY)
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
