package com.revpasswordmanager.repository;

import com.revpasswordmanager.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    List<VerificationCode> findByUser_Id(Long userId);

    VerificationCode findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    VerificationCode findByCodeAndUser_Id(String code, Long userId);

	VerificationCode findByUserIdAndCode(Long userId, String code);

}