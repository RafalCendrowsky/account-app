package com.rafalcendrowski.accountapp.model.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditEntityLogRepository extends JpaRepository<AuditEntityLog, String> {
    List<AuditEntityLog> getAuditEntityLogsByTimestampBetween(LocalDateTime from, LocalDateTime to);

    List<AuditEntityLog> getAuditEntityLogsByTimestampBetweenAndEntity(
            LocalDateTime from,
            LocalDateTime to,
            String entityType
    );
}
