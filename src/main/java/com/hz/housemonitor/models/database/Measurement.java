package com.hz.housemonitor.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(indexes =
        {@Index(name = "idx_Measurement_1", columnList = "sensor_event_id, type, value"),
         @Index(name = "idx_Measurement_2", columnList = "type, value"),
         @Index(name = "idx_Measurement_3", columnList = "sensor_event_id")
        })
@NoArgsConstructor
public class Measurement {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name="measurement_id")
    private Long id;
    private String unit;
    private String value;
    private String type;

    public Measurement(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Measurement that = (Measurement) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 598881245;
    }
}
