package com.hz.housemonitor.models.render;

import com.hz.housemonitor.models.database.projection.TemperatureStat;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Log4j2
public class SummaryStatistics {
    private String coldestRoom;
    private String hottestRoom;
    private BigDecimal lowestTemp = BigDecimal.valueOf(50);
    private BigDecimal highestTemp= BigDecimal.ZERO;
    private LocalDateTime lowestTempTime;
    private LocalDateTime highestTempTime;

    public void calculateMin(TemperatureStat temperatureStat) {
        if (temperatureStat != null) {
            calculate(temperatureStat.getLabel(), temperatureStat.getTemperature(), temperatureStat.getWhen());
        }
    }

    public void calculateMax(TemperatureStat temperatureStat) {
        if (temperatureStat != null) {
            calculate(temperatureStat.getLabel(), temperatureStat.getTemperature(), temperatureStat.getWhen());
        }
    }

    private void calculate(String room, BigDecimal temperature, LocalDateTime when) {
        if (temperature.compareTo(lowestTemp) < 0) {
            coldestRoom = room;
            lowestTemp = temperature;
            lowestTempTime = when;
        }
        if (temperature.compareTo(highestTemp) > 0) {
            hottestRoom = room;
            highestTemp = temperature;
            highestTempTime = when;
        }
    }
}
