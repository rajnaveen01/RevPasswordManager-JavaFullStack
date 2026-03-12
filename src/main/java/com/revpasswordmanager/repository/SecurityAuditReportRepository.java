package com.revpasswordmanager.repository;

import com.revpasswordmanager.entity.SecurityAuditReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityAuditReportRepository
        extends JpaRepository<SecurityAuditReport, Long> {

    List<SecurityAuditReport> findByUser_Id(Long userId);

    SecurityAuditReport findTopByUser_IdOrderByGeneratedAtDesc(Long userId);

}