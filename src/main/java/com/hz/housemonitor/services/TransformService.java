package com.hz.housemonitor.services;

import com.hz.housemonitor.models.database.Measurement;
import com.hz.housemonitor.models.database.SensorEvent;
import com.hz.housemonitor.models.hubitat.Attribute;
import com.hz.housemonitor.models.hubitat.Device;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
    public void mapMeasurements(SensorEvent sensorEvent, Device device) {
        sensorEvent.setMeasurementList(device.getAttributes().stream()
                .map(this::fromAttribute)
                .toList());
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
        } else if (attribute.getName().equalsIgnoreCase("voc")) {
            measurement.setUnit("ppm");
        } else if (attribute.getName().equalsIgnoreCase("carbondioxide")) {
            measurement.setUnit("ppm");
        } else if (attribute.getName().equalsIgnoreCase("pm25")) {
            measurement.setUnit("ug/m3");
        } else if (attribute.getName().equalsIgnoreCase("formaldehyde")) {
            measurement.setUnit("ug/m3");
        } else {
            measurement.setUnit("");
        }

        return measurement;
    }
}
