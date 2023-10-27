package com.hz.housemonitor.models.render;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
public class SensorCard {
    private String title;
    private String icon;
    private Long id;
    private List<SensorCardAttributeLine> attributes;
    private boolean isWeatherSensor;

    public SensorCard() {
        this.id = -1L;
        this.title = "";
        this.icon = "fas fa-cloud-sun";
        this.attributes = new ArrayList<>();

    }
    public SensorCard(Long id, String title, String icon, List<SensorCardAttributeLine> attributes, boolean isWeatherSensor) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.attributes = attributes;
        this.isWeatherSensor = isWeatherSensor;
    }

    public boolean isOffline() {
        return getAttributeValue("presence").equalsIgnoreCase("not present");
    }

    public boolean isBatteryLow() {
        if (getAttributeValue("battery").equalsIgnoreCase("not reported")) {
            return false;
        }
        try {
            return new DecimalFormat("##0.0 %").parse(getAttributeValue("battery")).floatValue() <= 0.3f;
        } catch (ParseException e) {
            log.error("Could not parse {}", e.getMessage());
            return false;
        }
    }

    public String getAttributeValue(String attribute) {
        return this.attributes == null ? "" : this.attributes.stream()
                .filter(a -> a.getText().equalsIgnoreCase(attribute))
                .findFirst()
                .orElse(new SensorCardAttributeLine("","",""))
                .getValue();
    }
}
