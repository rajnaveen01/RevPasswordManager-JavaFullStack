package com.revpasswordmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_report")
public class SecurityAuditReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="weak_passwords_count")
    private Integer weakPasswordsCount;

    @Column(name="reused_passwords_count")
    private Integer reusedPasswordsCount;

    @Column(name="old_passwords_count")
    private Integer oldPasswordsCount;

    @Column(name="generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public SecurityAuditReport() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Integer getWeakPasswordsCount() { return weakPasswordsCount; }

    public void setWeakPasswordsCount(Integer weakPasswordsCount) {
        this.weakPasswordsCount = weakPasswordsCount;
    }

    public Integer getReusedPasswordsCount() { return reusedPasswordsCount; }

    public void setReusedPasswordsCount(Integer reusedPasswordsCount) {
        this.reusedPasswordsCount = reusedPasswordsCount;
    }

    public Integer getOldPasswordsCount() { return oldPasswordsCount; }

    public void setOldPasswordsCount(Integer oldPasswordsCount) {
        this.oldPasswordsCount = oldPasswordsCount;
    }

    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}