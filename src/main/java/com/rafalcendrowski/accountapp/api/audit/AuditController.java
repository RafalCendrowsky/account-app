package com.rafalcendrowski.accountapp.api.audit;


import com.rafalcendrowski.accountapp.api.audit.response.AuditLogResponse;
import com.rafalcendrowski.accountapp.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('AUDITOR', 'ADMINISTRATOR')")
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditLogService auditLogService;

    @GetMapping("/logs")
    public List<AuditLogResponse> getAuditLogs() {
        return auditLogService.findAll();
    }
}
