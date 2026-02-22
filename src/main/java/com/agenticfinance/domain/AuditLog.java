package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Observer agent audit log: reasoning traces, hallucination alerts.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String agentId;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant timestamp;
}
