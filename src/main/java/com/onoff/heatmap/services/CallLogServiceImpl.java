package com.onoff.heatmap.services;

import com.onoff.heatmap.models.HourlyCallStatsDto;
import com.onoff.heatmap.repo.CallLogRepository;
import com.onoff.heatmap.models.HourlyCallStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallLogServiceImpl implements CallLogService {

    private final CallLogRepository callLogRepository;

    @Override
    public List<HourlyCallStatsDto> getHourlyStats(LocalDate date, int numberOfShades, int startHour, int endHour) {
        List<HourlyCallStats> rawStats = callLogRepository.getHourlyStatsByDateAndHourRange(date, startHour, endHour);

        Map<Integer, HourlyCallStats> statsMap = rawStats.stream()
                .collect(Collectors.toMap(HourlyCallStats::getHour, Function.identity()));

        List<HourlyCallStatsDto> result = new ArrayList<>();
        for (int hour = startHour; hour <= endHour; hour++) {
            HourlyCallStats stat = statsMap.get(hour);
            int answered = stat != null ? stat.getAnsweredCalls() : 0;
            int total = stat != null ? stat.getTotalCalls() : 0;
            result.add(new HourlyCallStatsDto(hour, answered, total, numberOfShades));
        }

        return result;
    }

    @Override
    public Page<HourlyCallStatsDto> getHourlyStatsPageable(LocalDate date, int numberOfShades, int startHour, int endHour, Pageable pageable) {
        Page<HourlyCallStats> page = callLogRepository.getHourlyStatsByDateAndHourRange(date, startHour, endHour, pageable);

        return page.map(stat -> new HourlyCallStatsDto(
                stat.getHour(),
                stat.getAnsweredCalls(),
                stat.getTotalCalls(),
                numberOfShades
        ));
    }



}
