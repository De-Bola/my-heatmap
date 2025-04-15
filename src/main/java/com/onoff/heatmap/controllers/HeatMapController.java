package com.onoff.heatmap.controllers;

import com.onoff.heatmap.controllers.response.SuccessResponse;
import com.onoff.heatmap.models.CallLogDto;
import com.onoff.heatmap.models.HourlyCallStatsDto;
import com.onoff.heatmap.services.CallLogService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/heatmap")
@Validated
public class HeatMapController {

    private static final Logger logger = LoggerFactory.getLogger(HeatMapController.class);

    private final CallLogService callLogService;

    public HeatMapController(CallLogService callLogService) {
        this.callLogService = callLogService;
    }

    @GetMapping
    public String getHeatMap(){
        return "Welcome to heatmap";
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getHeatMapByUsername(@AuthenticationPrincipal UserDetails user){
        logger.info("Logged in User is {}", user.getUsername());
        SuccessResponse<?> successResponse = new SuccessResponse<>(
                "Welcome to " + user.getUsername() + "'s heatmap", "" + HttpStatus.OK.value());
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/calls/logs/all")
    public ResponseEntity<SuccessResponse<?>> getCallLogs(@AuthenticationPrincipal UserDetails user){
        logger.info("Fetching call logs for {}", user.getUsername());
        List<CallLogDto> callLogs = callLogService.getCallLogs();
        SuccessResponse<?> successResponse = new SuccessResponse<>(
                callLogs, callLogs.size() + " entries found!"
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/calls/logs/pages")
    public ResponseEntity<SuccessResponse<?>> getCallLogs(Pageable pageable){
        logger.info("Fetching pageable call logs");
        Page<CallLogDto> callLogs = callLogService.getCallLogs(pageable);
        SuccessResponse<?> successResponse = new SuccessResponse<>(
                callLogs, callLogs.getTotalPages() + " pages of entries found!"
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/calls/logs")
    public ResponseEntity<SuccessResponse<?>> getCallLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateInput,
            Pageable pageable){
        logger.info("Fetching pageable call logs for date {}", dateInput);
        Page<CallLogDto> callLogs = callLogService.getCallLogsByDate(dateInput, pageable);
        SuccessResponse<?> successResponse = new SuccessResponse<>(
                callLogs, callLogs.getTotalPages() + " pages of entries found!"
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/answer-rate")
    public ResponseEntity<SuccessResponse<?>> getHourlyStats(
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                    message = "Invalid date format, expected yyyy-MM-dd") String dateInput,
            @RequestParam @Min(value = 3, message = "Minimum numberOfShades is 3")
            @Max(value = 10, message = "Maximum numberOfShades is 10") int numberOfShades,
            @RequestParam(required = false) @Min(0) @Max(23) Integer startHour,
            @RequestParam(required = false) @Min(0) @Max(23) Integer endHour
    ) {
        // setting default values
        int fromHour = startHour != null ? startHour : 0;
        int toHour = endHour != null ? endHour : 23;

        if (toHour < fromHour) {
            throw new IllegalArgumentException("endHour must be greater than or equal to startHour");
        }

        LocalDate date = LocalDate.parse(dateInput);

        List<HourlyCallStatsDto> stats = callLogService.getHourlyStats(date, numberOfShades, fromHour, toHour);
        SuccessResponse<List<HourlyCallStatsDto>> response = new SuccessResponse<>(
                stats,
                String.format("%d hourly entries between %02d and %02d.", stats.size(), fromHour, toHour)
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
