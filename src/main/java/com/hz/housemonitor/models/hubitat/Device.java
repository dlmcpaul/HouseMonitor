package com.hz.housemonitor.models.hubitat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private String name;
    private String label;
    private String type;
    private String id;
    private List<Capability> capabilities;
    private List<Attribute> attributes;

    @Override
    public String toString() {
        return name + " : "
                + label + " : "
                + type + " : "
                + id + "{" +
                attributes.toString() + "}";
    }
}
