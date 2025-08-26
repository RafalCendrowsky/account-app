package com.rafalcendrowski.accountapp.api.audit.response;

import java.time.LocalDateTime;

public record AuditLogResponse(
        String entity,
        String entityId,
        String action,
        String user,
        LocalDateTime timestamp,
        String data
) {
}
