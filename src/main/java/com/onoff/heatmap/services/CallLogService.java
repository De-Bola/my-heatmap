package com.onoff.heatmap.services;

import com.onoff.heatmap.models.CallLog;
import com.onoff.heatmap.models.CallLogDto;
import com.onoff.heatmap.models.HourlyCallStatsDto;
import com.onoff.heatmap.repo.CallLogRepository;
import com.onoff.heatmap.repo.HourlyCallStats;
import com.onoff.heatmap.utils.CallLogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CallLogService {

    private final CallLogRepository callLogRepository;
    private final CallLogMapper callLogMapper;

    public CallLogService(CallLogRepository callLogRepository, CallLogMapper callLogMapper) {
        this.callLogRepository = callLogRepository;
        this.callLogMapper = callLogMapper;
    }

    public List<CallLogDto> getCallLogs(){
        List<CallLog> callLogs = callLogRepository.findAll();
        return callLogMapper.toDtoList(callLogs);
    }

    public Page<CallLogDto> getCallLogs(Pageable pageable){
        Page<CallLog> callLogs = callLogRepository.findAll(pageable);
        return callLogs.map(callLogMapper::toDto);
    }

    public Page<CallLogDto> getCallLogsByDate(String dateInput, Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateInput, formatter);

        Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(date.plusDays(1).atStartOfDay().minusNanos(1));

        Page<CallLog> callLogs = callLogRepository.findByStartedAtBetween(startOfDay, endOfDay, pageable);
        return callLogs.map(callLogMapper::toDto);
    }

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


}
