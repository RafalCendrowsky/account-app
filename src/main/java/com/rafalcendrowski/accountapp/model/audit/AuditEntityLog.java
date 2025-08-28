package com.rafalcendrowski.accountapp.model.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_entity_logs")
public class AuditEntityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private EntityEventType eventType;

    private String entity;
    private String entityId;
    private String user;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "jsonb")
    private String data;
}
