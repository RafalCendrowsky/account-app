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
        var oldMap = new HashMap<String, Object>();
        var newMap = new HashMap<String, Object>();

        for (int index : event.getDirtyProperties()) {
            String property = propertyNames[index];
            oldMap.put(property, event.getOldState()[index]);
            newMap.put(property, event.getState()[index]);
        }
        var changes = Map.of(
                "old", oldMap,
                "new", newMap
        );
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
