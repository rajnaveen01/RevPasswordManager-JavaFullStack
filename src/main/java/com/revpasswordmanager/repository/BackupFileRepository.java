package com.revpasswordmanager.repository;

import com.revpasswordmanager.entity.BackupFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BackupFileRepository extends JpaRepository<BackupFile, Long> {

    List<BackupFile> findByUser_Id(Long userId);

    BackupFile findTopByUser_IdOrderByCreatedAtDesc(Long userId);

}