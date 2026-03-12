package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.entity.SecurityAuditReport;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.entity.VaultEntry;
import com.revpasswordmanager.repository.SecurityAuditReportRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.repository.VaultEntryRepository;
import com.revpasswordmanager.service.AuditService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuditServiceImpl implements AuditService {

    private final VaultEntryRepository vaultEntryRepository;
    private final UserRepository userRepository;
    private final SecurityAuditReportRepository auditRepository;

    public AuditServiceImpl(VaultEntryRepository vaultEntryRepository,
                            UserRepository userRepository,
                            SecurityAuditReportRepository auditRepository) {
        this.vaultEntryRepository = vaultEntryRepository;
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public SecurityAuditReport generateAuditReport(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<VaultEntry> entries = vaultEntryRepository.findByUser_Id(userId);

        // WEAK: passwordStrength is WEAK or null
        int weakCount = (int) entries.stream()
                .filter(e -> e.getPasswordStrength() == null
                        || e.getPasswordStrength().equalsIgnoreCase("WEAK"))
                .count();

        // REUSED: same encrypted password used more than once
        Set<String> seen = new HashSet<>();
        Set<String> reused = new HashSet<>();
        for (VaultEntry e : entries) {
            String pwd = e.getEncryptedPassword();
            if (pwd != null) {
                if (!seen.add(pwd)) {
                    reused.add(pwd);
                }
            }
        }
        int reusedCount = reused.size();

        // OLD: not updated in more than 90 days
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        int oldCount = (int) entries.stream()
                .filter(e -> e.getUpdatedAt() != null
                        && e.getUpdatedAt().isBefore(ninetyDaysAgo))
                .count();

        SecurityAuditReport report = new SecurityAuditReport();
        report.setWeakPasswordsCount(weakCount);
        report.setReusedPasswordsCount(reusedCount);
        report.setOldPasswordsCount(oldCount);
        report.setGeneratedAt(LocalDateTime.now());
        report.setUser(user);

        return auditRepository.save(report);
    }
}