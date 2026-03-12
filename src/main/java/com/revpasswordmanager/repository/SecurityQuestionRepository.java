package com.revpasswordmanager.repository;

import com.revpasswordmanager.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

    List<SecurityQuestion> findByUser_Id(Long userId);

}