package com.rafalcendrowski.accountapp.audit;

import com.rafalcendrowski.accountapp.model.audit.AuditEntityLog;
import com.rafalcendrowski.accountapp.model.audit.EntityEventType;
import com.rafalcendrowski.accountapp.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditEntityEventListener implements PostUpdateEventListener, PostDeleteEventListener, PostInsertEventListener {
    private final AuditLogService auditLogService;

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditEntityLog.class)
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

        auditLogService.logEntityEvent(entity, (String) event.getId(), EntityEventType.UPDATE, changes);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditEntityLog.class)
            return;

        auditLogService.logEntityEvent(entity, (String) event.getId(), EntityEventType.DELETE);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        var entity = event.getEntity();

        if (entity.getClass() == AuditEntityLog.class)
            return;

        auditLogService.logEntityEvent(entity, (String) event.getId(), EntityEventType.CREATE);
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return true;
    }
}
