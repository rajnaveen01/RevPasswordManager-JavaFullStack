//package com.revpasswordmanager.service;
//
//import com.revpasswordmanager.dto.SecurityAnswerDTO;
//import com.revpasswordmanager.dto.SecurityQuestionDTO;
//
//import java.util.List;
//
//public interface SecurityService {
//
//    // Add security question
//    void addSecurityQuestion(SecurityQuestionDTO dto);
//
//    // Get questions only (answers hidden)
//    List<String> getSecurityQuestions(Long userId);
//
//    // Generate verification code (OTP simulation)
//    String generateVerificationCode(Long userId, String purpose);
//    String generateAuditReport(Long userId);
//    Long getUserIdByUsername(String username);
//    boolean verifyAnswers(SecurityAnswerDTO dto);
//
//    void resetPassword(SecurityAnswerDTO dto);
//
//	boolean verifyCode(Long userId, String code);
//
//}



package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.SecurityAnswerDTO;
import com.revpasswordmanager.dto.SecurityQuestionDTO;

import java.util.List;

public interface SecurityService {

    // Add security question
    void addSecurityQuestion(SecurityQuestionDTO dto);

    // Get questions only (answers hidden)
    List<String> getSecurityQuestions(Long userId);

    // Generate verification code (OTP simulation)
    String generateVerificationCode(Long userId, String purpose);
    String generateAuditReport(Long userId);
    Long getUserIdByUsername(String username);
    boolean verifyAnswers(SecurityAnswerDTO dto);

    void resetPassword(SecurityAnswerDTO dto);

    // Get questions with IDs (for profile view/edit)
    List<java.util.Map<String, Object>> getSecurityQuestionsWithIds(Long userId);

    // Update a single security answer
    void updateSecurityAnswer(Long questionId, String newAnswer);

	boolean verifyCode(Long userId, String code);

}