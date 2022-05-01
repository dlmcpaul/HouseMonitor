package com.hz.housemonitor.models.database.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TemperatureStat {
    String getLabel();
    BigDecimal getTemperature();
    LocalDateTime getWhen();
}
