package com.agenticfinance.repository;

import com.agenticfinance.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    List<AuditLog> findByAgentIdAndTimestampAfterOrderByTimestampDesc(String agentId, Instant after, org.springframework.data.domain.Pageable pageable);
}
