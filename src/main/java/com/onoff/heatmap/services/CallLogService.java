package com.onoff.heatmap.services;

import com.onoff.heatmap.models.HourlyCallStatsDto;

import java.time.LocalDate;
import java.util.List;

public interface CallLogService {
    List<HourlyCallStatsDto> getHourlyStats(LocalDate date, int numberOfShades, int startHour, int endHour);
}
