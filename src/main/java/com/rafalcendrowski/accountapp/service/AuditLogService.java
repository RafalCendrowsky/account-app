package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.audit.response.AuditLogResponse;
import com.rafalcendrowski.accountapp.mapper.AuditLogMapper;
import com.rafalcendrowski.accountapp.model.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public List<AuditLogResponse> findAll() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }
}
