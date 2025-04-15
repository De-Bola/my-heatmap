package com.onoff.heatmap.repo;

public interface HourlyCallStats {
    Integer getHour();
    Integer getAnsweredCalls();
    Integer getTotalCalls();
}
