package com.rafalcendrowski.accountapp.mapper;

import com.rafalcendrowski.accountapp.api.audit.response.AuditLogResponse;
import com.rafalcendrowski.accountapp.model.audit.AuditLog;
import org.mapstruct.Mapper;

@Mapper
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}
