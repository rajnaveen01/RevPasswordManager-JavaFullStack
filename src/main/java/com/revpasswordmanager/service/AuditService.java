package com.revpasswordmanager.service;

import com.revpasswordmanager.entity.SecurityAuditReport;

public interface AuditService {

    SecurityAuditReport generateAuditReport(Long userId);

}