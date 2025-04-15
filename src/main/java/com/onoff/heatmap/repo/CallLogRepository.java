package com.onoff.heatmap.repo;

import com.onoff.heatmap.models.CallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, UUID> {
    Page<CallLog> findByStartedAtBetween(Timestamp startedAt, Timestamp endedAt, Pageable pageable);

    @Query(value = """
    SELECT
        EXTRACT(HOUR FROM started_at) AS hour,
        SUM(CASE WHEN status = 'ANSWERED' THEN 1 ELSE 0 END) AS answeredCalls,
        COUNT(*) AS totalCalls
    FROM call_log
    WHERE CAST(started_at AS DATE) = :targetDate
      AND EXTRACT(HOUR FROM started_at) BETWEEN :startHour AND :endHour
    GROUP BY EXTRACT(HOUR FROM started_at)
    ORDER BY hour
    """, nativeQuery = true)
    List<HourlyCallStats> getHourlyStatsByDateAndHourRange(
            @Param("targetDate") LocalDate date,
            @Param("startHour") int startHour,
            @Param("endHour") int endHour
    );
}
