//package com.revpasswordmanager.serviceimpl;
//
//import com.revpasswordmanager.dto.SecurityAnswerDTO;
//
//import com.revpasswordmanager.dto.SecurityQuestionDTO;
//import com.revpasswordmanager.entity.SecurityQuestion;
//import com.revpasswordmanager.entity.User;
//import com.revpasswordmanager.entity.VerificationCode;
//import com.revpasswordmanager.repository.SecurityQuestionRepository;
//import com.revpasswordmanager.repository.UserRepository;
//import com.revpasswordmanager.repository.VerificationCodeRepository;
//import com.revpasswordmanager.service.EmailService;
//import com.revpasswordmanager.service.SecurityService;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//
//@Service
//public class SecurityServiceImpl implements SecurityService {
//
//    private final SecurityQuestionRepository questionRepository;
//    private final UserRepository userRepository;
//    private final VerificationCodeRepository verificationCodeRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final SecurityQuestionRepository securityQuestionRepository;
//    private final EmailService emailService;
////    private final SecurityQuestionRepository questionRepository;
////    private final SecurityQuestionRepository securityQuestionRepository;
//
//    public SecurityServiceImpl(SecurityQuestionRepository questionRepository,
//    		SecurityQuestionRepository securityQuestionRepository,
//                               UserRepository userRepository,
//                               VerificationCodeRepository verificationCodeRepository,
//                               PasswordEncoder passwordEncoder,
//                               EmailService emailService) {
//
//        this.questionRepository = questionRepository;
//        this.userRepository = userRepository;
//        this.verificationCodeRepository = verificationCodeRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.securityQuestionRepository = securityQuestionRepository;	
//        this.emailService = emailService;
//    }
//
//    // ADD SECURITY QUESTION
//    @Override
//    public void addSecurityQuestion(SecurityQuestionDTO dto) {
//
//        User user = userRepository.findById(dto.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        SecurityQuestion securityQuestion = new SecurityQuestion();
//        securityQuestion.setQuestion(dto.getQuestion());
//        securityQuestion.setAnswerHash(passwordEncoder.encode(dto.getAnswer()));
//        securityQuestion.setUser(user);
//
//        questionRepository.save(securityQuestion);
//    }
//
//    // GET QUESTIONS
//    @Override
//    public List<String> getSecurityQuestions(Long userId) {
//
//        return questionRepository.findByUser_Id(userId)
//                .stream()
//                .map(SecurityQuestion::getQuestion)
//                .collect(Collectors.toList());
//    }
//    @Override
//    public String generateAuditReport(Long userId) {
//
//        return "Security audit completed for user " + userId;
//    }
//
//    // GENERATE VERIFICATION CODE
//    @Override
//    public String generateVerificationCode(Long userId, String purpose) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String code = String.valueOf(new Random().nextInt(900000) + 100000);
//
//        VerificationCode verificationCode = new VerificationCode();
//        verificationCode.setCode(code);
//        verificationCode.setPurpose(purpose);
//        verificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(5));
//        verificationCode.setIsUsed(false);
//        verificationCode.setUser(user);
//
//        verificationCodeRepository.save(verificationCode);
//
//        // --- NEW EMAIL LOGIC ---
//        try {
//            // This calls your new EmailService to send the code to the user's email
//            emailService.sendVerificationEmail(user.getEmail(), code);
//            System.out.println("Verification email successfully sent to: " + user.getEmail());
//        } catch (Exception e) {
//            System.err.println("Failed to send email: " + e.getMessage());
//        }
//        // -----------------------
//
//        return code;
//    }
//    @Override
//    public Long getUserIdByUsername(String username) {
//
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"))
//                .getId();
//    }
//    
//    @Override
//    public boolean verifyAnswers(SecurityAnswerDTO dto) {
//
//        User user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<SecurityQuestion> questions =
//                securityQuestionRepository.findByUser_Id(user.getId());
//
//        boolean a1 = passwordEncoder.matches(dto.getAnswer1(), questions.get(0).getAnswerHash());
//        boolean a2 = passwordEncoder.matches(dto.getAnswer2(), questions.get(1).getAnswerHash());
//        boolean a3 = passwordEncoder.matches(dto.getAnswer3(), questions.get(2).getAnswerHash());
//
//        return a1 && a2 && a3;
//    }
//
//    @Override
//    public void resetPassword(SecurityAnswerDTO dto) {
//
//        User user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
//
//        userRepository.save(user);
//    }
//    
//    @Override
//    public boolean verifyCode(Long userId, String code) {
//
//        VerificationCode verificationCode =
//                verificationCodeRepository.findByUserIdAndCode(userId, code);
//
//        if (verificationCode == null) {
//            return false;
//        }
//
//        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
//            return false;
//        }
//
//        verificationCode.setIsUsed(true);
//        verificationCodeRepository.save(verificationCode);
//
//        return true;
//    }
//    
//    
//}












package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.dto.SecurityAnswerDTO;

import com.revpasswordmanager.dto.SecurityQuestionDTO;
import com.revpasswordmanager.entity.SecurityQuestion;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.entity.VerificationCode;
import com.revpasswordmanager.repository.SecurityQuestionRepository;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.repository.VerificationCodeRepository;
import com.revpasswordmanager.service.EmailService;
import com.revpasswordmanager.service.SecurityService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class SecurityServiceImpl implements SecurityService {

    private final SecurityQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final EmailService emailService;
//    private final SecurityQuestionRepository questionRepository;
//    private final SecurityQuestionRepository securityQuestionRepository;

    public SecurityServiceImpl(SecurityQuestionRepository questionRepository,
    		SecurityQuestionRepository securityQuestionRepository,
                               UserRepository userRepository,
                               VerificationCodeRepository verificationCodeRepository,
                               PasswordEncoder passwordEncoder,
                               EmailService emailService) {

        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityQuestionRepository = securityQuestionRepository;	
        this.emailService = emailService;
    }

    // GET QUESTIONS WITH IDs
    @Override
    public List<java.util.Map<String, Object>> getSecurityQuestionsWithIds(Long userId) {
        return questionRepository.findByUser_Id(userId)
                .stream()
                .map(q -> {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id", q.getId());
                    map.put("question", q.getQuestion());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // UPDATE SECURITY ANSWER
    @Override
    public void updateSecurityAnswer(Long questionId, String newAnswer) {
        if (newAnswer == null || newAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty.");
        }
        SecurityQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Security question not found"));
        question.setAnswerHash(passwordEncoder.encode(newAnswer.trim()));
        questionRepository.save(question);
    }

    // ADD SECURITY QUESTION
    @Override
    public void addSecurityQuestion(SecurityQuestionDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setQuestion(dto.getQuestion());
        securityQuestion.setAnswerHash(passwordEncoder.encode(dto.getAnswer()));
        securityQuestion.setUser(user);

        questionRepository.save(securityQuestion);
    }

    // GET QUESTIONS
    @Override
    public List<String> getSecurityQuestions(Long userId) {

        return questionRepository.findByUser_Id(userId)
                .stream()
                .map(SecurityQuestion::getQuestion)
                .collect(Collectors.toList());
    }
    @Override
    public String generateAuditReport(Long userId) {

        return "Security audit completed for user " + userId;
    }

    // GENERATE VERIFICATION CODE
    @Override
    public String generateVerificationCode(Long userId, String purpose) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setPurpose(purpose);
        verificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        verificationCode.setIsUsed(false);
        verificationCode.setUser(user);

        verificationCodeRepository.save(verificationCode);

        // --- NEW EMAIL LOGIC ---
        try {
            // This calls your new EmailService to send the code to the user's email
            emailService.sendVerificationEmail(user.getEmail(), code);
            System.out.println("Verification email successfully sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
        // -----------------------

        return code;
    }
    @Override
    public Long getUserIdByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
    
    @Override
    public boolean verifyAnswers(SecurityAnswerDTO dto) {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SecurityQuestion> questions =
                securityQuestionRepository.findByUser_Id(user.getId());

        boolean a1 = passwordEncoder.matches(dto.getAnswer1(), questions.get(0).getAnswerHash());
        boolean a2 = passwordEncoder.matches(dto.getAnswer2(), questions.get(1).getAnswerHash());
        boolean a3 = passwordEncoder.matches(dto.getAnswer3(), questions.get(2).getAnswerHash());

        return a1 && a2 && a3;
    }

    @Override
    public void resetPassword(SecurityAnswerDTO dto) {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }
    
    @Override
    public boolean verifyCode(Long userId, String code) {

        VerificationCode verificationCode =
                verificationCodeRepository.findByUserIdAndCode(userId, code);

        if (verificationCode == null) {
            return false;
        }

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        verificationCode.setIsUsed(true);
        verificationCodeRepository.save(verificationCode);

        return true;
    }
    
    
}