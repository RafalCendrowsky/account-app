package com.rafalcendrowski.accountapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafalcendrowski.accountapp.api.audit.response.AuditLogResponse;
import com.rafalcendrowski.accountapp.mapper.AuditLogMapper;
import com.rafalcendrowski.accountapp.model.audit.AuditEntityLog;
import com.rafalcendrowski.accountapp.model.audit.AuditEntityLogRepository;
import com.rafalcendrowski.accountapp.model.audit.EntityEventType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditEntityLogRepository auditEntityLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    public List<AuditLogResponse> findAll(LocalDateTime from, LocalDateTime to, @Nullable String entityType) {
        var logs = entityType != null ?
                auditEntityLogRepository.getAuditEntityLogsByTimestampBetweenAndEntity(from, to, entityType) :
                auditEntityLogRepository.getAuditEntityLogsByTimestampBetween(from, to);

        return logs.stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    public void logEntityEvent(Object entity, String entityId, EntityEventType eventType) {
        logEntityEvent(entity, entityId, eventType, entity);
    }

    public void logEntityEvent(Object entity, String entityId, EntityEventType eventType, Object data) {
        var log = new AuditEntityLog();
        log.setEntity(entity.getClass().getSimpleName());
        log.setEntityId(entityId);
        log.setEventType(eventType);
        log.setTimestamp(LocalDateTime.now());
        log.setUser(getPrincipal());
        try {
            log.setData(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.setData("{}");
        }
        auditEntityLogRepository.save(log);
    }

    private String getPrincipal() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("system");
    }
}
