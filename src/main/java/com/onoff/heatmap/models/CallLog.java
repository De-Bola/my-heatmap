package com.onoff.heatmap.models;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CALL_LOG", indexes = {
        @Index(name = "idx_user_id", columnList = "USER_ID"),
        @Index(name = "idx_status", columnList = "STATUS"),
        @Index(name = "idx_started_at", columnList = "STARTED_AT")
})
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 45, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "USER_ID", length = 45, nullable = false)
    private String userId;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ONOFF_NUMBER", length = 45, nullable = false)
    private String onOffNumber;

    @Column(name = "CONTACT_NUMBER", length = 45, nullable = false)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 45, nullable = false)
    private Status status;

    @Column(name = "INCOMING", nullable = false)
    private boolean isIncoming;

    @Column(name = "DURATION", nullable = false)
    private int duration;

    @Column(name = "STARTED_AT", columnDefinition = "TIMESTAMP(3)", nullable = false)
    private Timestamp startedAt;

    @Column(name = "ENDED_AT", columnDefinition = "TIMESTAMP(3)", nullable = false)
    private Timestamp endedAt;
}
