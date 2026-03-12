//package com.revpasswordmanager.repository;
//
//import com.revpasswordmanager.entity.VaultEntry;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//
//public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {
//
//    List<VaultEntry> findByUser_Id(Long userId);
//    long countByUser_Id(Long userId);
//
//    List<VaultEntry> findByUser_IdAndIsFavoriteTrue(Long userId);
//
//    List<VaultEntry> findByUser_IdAndAccountNameContainingIgnoreCase(Long userId, String accountName);
//
//    List<VaultEntry> findByUser_IdAndCategory_Id(Long userId, Long categoryId);
//
//}




package com.revpasswordmanager.repository;

import com.revpasswordmanager.entity.VaultEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {

    List<VaultEntry> findByUser_Id(Long userId);
    long countByUser_Id(Long userId);

    List<VaultEntry> findByUser_IdAndIsFavoriteTrue(Long userId);

    List<VaultEntry> findByUser_IdAndAccountNameContainingIgnoreCase(Long userId, String accountName);

    List<VaultEntry> findByUser_IdAndCategory_Id(Long userId, Long categoryId);

    // Duplicate check: same user + same accountName + same username
    Optional<VaultEntry> findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCase(
            Long userId, String accountName, String username);

    // Duplicate check on update: same user + same accountName + same username but different entry
    List<VaultEntry> findByUser_IdAndAccountNameIgnoreCaseAndUsernameIgnoreCaseAndIdNot(
            Long userId, String accountName, String username, Long excludeId);

}