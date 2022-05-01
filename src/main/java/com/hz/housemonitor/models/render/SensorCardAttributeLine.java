package com.hz.housemonitor.models.render;

import lombok.Data;

@Data
public class SensorCardAttributeLine {
    private String icon;
    private String text;
    private String value;

    public SensorCardAttributeLine(String text, String value, String unit) {
        this.text = text.isEmpty() ? "" : text.substring(0, 1).toUpperCase() + text.substring(1);
        this.value = mapValue(value, unit);
        this.icon = "fas " + mapIcon(value, unit);
    }

    private String mapIcon(String value, String unit) {
        if (text.equalsIgnoreCase("temperature")) {
            return "fa-thermometer-" + mapRemaining(scaleToPercentage(Double.valueOf(value).intValue(), unit));
        } else if (text.equalsIgnoreCase("humidity")) {
            return "fa-water";
        } else if (text.equalsIgnoreCase("battery")) {
            return "fa-battery-" + mapRemaining(Double.valueOf(value).intValue());
        } else if (text.equalsIgnoreCase("voltage")) {
            return "fa-bolt";
        } else if (text.equalsIgnoreCase("presence")) {
            if (value.equalsIgnoreCase("present")) {
                return "fa-toggle-on";
            } else {
                return "fa-toggle-off";
            }
        } else if (text.equalsIgnoreCase("pressure")) {
            return "fa-cloud";
        } else if (text.equalsIgnoreCase("winddirection")) {
            return "fa-compass";
        } else if (text.equalsIgnoreCase("windspeed")) {
            return "fa-tachometer-alt";
        } else if (text.equalsIgnoreCase("weather")) {
            return "fa-cloud-sun-rain";
        } else {
            return "fa-question-circle";
        }
    }

    private int scaleToPercentage(int amount, String unit) {
        if (unit.equalsIgnoreCase("celsius")) {
            return amount * 100 / 40;
        }
        return amount;
    }

    private String mapRemaining(int amount) {
        if (amount < 100) {
            if (amount < 75) {
                if (amount < 50) {
                    if (amount < 25) {
                        return "empty";
                    }
                    return "quarter";
                }
                return "half";
            }
            return "three-quarters";
        }
        return "full";
    }

    private String mapValue(String value, String unit) {
        if (value == null) {
            return "Not Reported";
        }

        if (unit.equalsIgnoreCase("volt")) {
            return value + " V";
        } else if (unit.equalsIgnoreCase("percent")) {
            return value + " %";
        } else if (unit.equalsIgnoreCase("hectopascal")) {
            return value + " hPa";
        } else if (unit.equalsIgnoreCase("celsius")) {
            return value + " " + "\u00B0" + "C";
        } else if (unit.equalsIgnoreCase("fahrenheit")) {
            return value + " " + "\u00B0" + "F";
        }
        return value;
    }

}
