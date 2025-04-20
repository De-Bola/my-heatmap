package com.onoff.heatmap.services;

import com.onoff.heatmap.models.HourlyCallStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CallLogService {
    List<HourlyCallStatsDto> getHourlyStats(LocalDate date, int numberOfShades, int startHour, int endHour);
    Page<HourlyCallStatsDto> getHourlyStatsPageable(LocalDate date, int numberOfShades, int startHour, int endHour, Pageable pageable);
}
