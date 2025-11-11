package com.rafalcendrowski.accountapp.api.common;

import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record AuditResponse<T>(
        T entity,
        Long revisionId,
        LocalDateTime revisionTimestamp,
        RevisionMetadata.RevisionType revisionType
) {
    public static <T> AuditResponse<T> from(T mappedEntity, RevisionMetadata<Long> metadata) {
        return new AuditResponse<>(
                mappedEntity,
                metadata.getRequiredRevisionNumber(),
                LocalDateTime.ofInstant(metadata.getRequiredRevisionInstant(), ZoneOffset.UTC),
                metadata.getRevisionType()
        );
    }
}