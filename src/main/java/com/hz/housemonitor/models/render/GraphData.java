package com.hz.housemonitor.models.render;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hz.housemonitor.models.database.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GraphData {
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'.000'")
    private LocalDateTime when;
    private String value;
    private String measurementType;
    private String measurementUnit;

    public GraphData(LocalDateTime when, Measurement measurement) {
        this.when = when;
        this.value = measurement.getValue();
        this.measurementUnit = measurement.getUnit();
        this.measurementType = measurement.getType();
    }
}
