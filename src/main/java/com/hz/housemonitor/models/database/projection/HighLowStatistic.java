package com.hz.housemonitor.models.database.projection;

import java.math.BigDecimal;

public interface HighLowStatistic {
    String getLabel();
    BigDecimal getMin();
    BigDecimal getMax();
}
