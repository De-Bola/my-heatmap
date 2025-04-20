package com.onoff.heatmap.controllers;

import com.onoff.heatmap.config.RequestConstraintsProps;
import com.onoff.heatmap.controllers.response.SuccessResponse;
import com.onoff.heatmap.models.HourlyCallStatsDto;
import com.onoff.heatmap.models.RequestDto;
import com.onoff.heatmap.services.CallLogServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/heatmap")
@RestController
@Validated
@Slf4j
public class HeatMapController {

    private final CallLogServiceImpl callLogService;

    private final RequestConstraintsProps constraints;

    public HeatMapController(CallLogServiceImpl callLogService,
                             @Qualifier("heatmap.constraints-com.onoff.heatmap.config.RequestConstraintsProps") RequestConstraintsProps constraints) {
        this.callLogService = callLogService;
        this.constraints = constraints;
    }


    @GetMapping("/answer-rate")
    public ResponseEntity<SuccessResponse<?>> getAnswerRate(@Valid RequestDto request) {
        RequestDto validatedRequest = request.normalize(constraints);

        List<HourlyCallStatsDto> stats = callLogService.getHourlyStats(
                validatedRequest.dateInput(),
                validatedRequest.numberOfShades(),
                validatedRequest.startHour(),
                validatedRequest.endHour());
        SuccessResponse<List<HourlyCallStatsDto>> response = new SuccessResponse<>(
                stats,
                String.format("%d hourly entries between %02d and %02d.",
                        stats.size(),
                        validatedRequest.startHour(),
                        validatedRequest.endHour())
        );

        return ResponseEntity.ok(response);
    }

}
