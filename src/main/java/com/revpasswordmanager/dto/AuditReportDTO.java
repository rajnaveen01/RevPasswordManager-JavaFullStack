package com.revpasswordmanager.dto;

import java.time.LocalDateTime;

public class AuditReportDTO {

    private int weakPasswordsCount;
    private int reusedPasswordsCount;
    private int oldPasswordsCount;
    private LocalDateTime generatedAt;

    public AuditReportDTO() {}

    public int getWeakPasswordsCount() {
        return weakPasswordsCount;
    }

    public void setWeakPasswordsCount(int weakPasswordsCount) {
        this.weakPasswordsCount = weakPasswordsCount;
    }

    public int getReusedPasswordsCount() {
        return reusedPasswordsCount;
    }

    public void setReusedPasswordsCount(int reusedPasswordsCount) {
        this.reusedPasswordsCount = reusedPasswordsCount;
    }

    public int getOldPasswordsCount() {
        return oldPasswordsCount;
    }

    public void setOldPasswordsCount(int oldPasswordsCount) {
        this.oldPasswordsCount = oldPasswordsCount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}