package com.hz.housemonitor.models.hubitat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    private String name;
    private String currentValue;
    private String dataType;
}
