package com.hz.housemonitor.models.render;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorHeader {
    private long id;
    private String title;
    private String value;
    private String type;
}
