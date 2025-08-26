package com.rafalcendrowski.accountapp.model.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String entity;
    private String entityId;
    private String action;
    private String user;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "jsonb")
    private String data;
}
