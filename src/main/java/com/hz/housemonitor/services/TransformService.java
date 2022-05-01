package com.hz.housemonitor.services;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.hubitat.Attribute;
import com.hz.housemonitor.models.hubitat.Device;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TransformService {
    public void mapMeasurements(SensorEvent sensorEvent, Device device) {
        sensorEvent.setMeasurementList(device.getAttributes().stream()
                .map(this::fromAttribute)
                .collect(Collectors.toList()));
    }

    private Measurement fromAttribute(Attribute attribute) {
        Measurement measurement = new Measurement();
        measurement.setType(attribute.getName());
        measurement.setValue(attribute.getCurrentValue());
        if (attribute.getName().equalsIgnoreCase("temperature")) {
            measurement.setUnit("Celsius");
        } else if (attribute.getName().equalsIgnoreCase("battery")) {
            measurement.setUnit("Percent");
        } else if (attribute.getName().equalsIgnoreCase("voltage")) {
            measurement.setUnit("Volt");
        } else if (attribute.getName().equalsIgnoreCase("presence")) {
            measurement.setUnit("String");
        } else if (attribute.getName().equalsIgnoreCase("humidity")) {
            measurement.setUnit("Percent");
        } else if (attribute.getName().equalsIgnoreCase("pressure")) {
            measurement.setUnit("Hectopascal");
        } else {
            measurement.setUnit("");
        }

        return measurement;
    }
}
