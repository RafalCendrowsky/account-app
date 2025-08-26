package com.rafalcendrowski.accountapp.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafalcendrowski.accountapp.model.audit.AuditLog;
import com.rafalcendrowski.accountapp.model.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditEventListener implements PostUpdateEventListener, PostDeleteEventListener, PostInsertEventListener {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditLog.class)
            return;

        var propertyNames = event.getPersister().getPropertyNames();
        var dirtyPropertiesIndices = event.getDirtyProperties();

        Map<String, Object> changes = new HashMap<>();
        for (int index : dirtyPropertiesIndices) {
            String property = propertyNames[index];
            Object oldVal = event.getOldState()[index];
            Object newVal = event.getState()[index];
            changes.put(property, Map.of("old", oldVal, "new", newVal));
        }

        var log = getAuditLog(entity, event.getId(), "UPDATE", changes);
        auditLogRepository.save(log);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditLog.class)
            return;

        var log = getAuditLog(entity, event.getId(), "DELETE", entity);
        auditLogRepository.save(log);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditLog.class)
            return;

        var log = getAuditLog(entity, event.getId(), "INSERT", entity);
        auditLogRepository.save(log);
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return true;
    }

    private AuditLog getAuditLog(Object entity, Object id, String action, Object data) {
        var user = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("system");

        var log = new AuditLog();
        log.setEntity(entity.getClass().getSimpleName());
        log.setEntityId((String) id);
        log.setAction("UPDATE");
        log.setTimestamp(LocalDateTime.now());
        log.setUser(user);
        try {
            log.setData(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.setData("{}");
        }
        return log;
    }
}
