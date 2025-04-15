package com.onoff.heatmap.models;

import java.sql.Timestamp;

public record CallLogDto(
        String id,
        String userId,
        String username,
        String onOffNumber,
        String contactNumber,
        String status,
        boolean incoming,
        int duration,
        Timestamp startedAt,
        Timestamp endedAt
) {}
