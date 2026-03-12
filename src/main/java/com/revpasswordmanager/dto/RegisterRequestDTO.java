package com.revpasswordmanager.dto;

import jakarta.validation.constraints.*;

public class RegisterRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscore")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Master password is required")
    @Size(min = 8, message = "Master password must be at least 8 characters")
    private String masterPassword;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    // Security Questions
    @NotBlank(message = "Security Question 1 is required")
    private String question1;

    @NotBlank(message = "Answer 1 is required")
    @Size(min = 2, message = "Answer must be at least 2 characters")
    private String answer1;

    @NotBlank(message = "Security Question 2 is required")
    private String question2;

    @NotBlank(message = "Answer 2 is required")
    @Size(min = 2, message = "Answer must be at least 2 characters")
    private String answer2;

    @NotBlank(message = "Security Question 3 is required")
    private String question3;

    @NotBlank(message = "Answer 3 is required")
    @Size(min = 2, message = "Answer must be at least 2 characters")
    private String answer3;

    public RegisterRequestDTO() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMasterPassword() { return masterPassword; }
    public void setMasterPassword(String masterPassword) { this.masterPassword = masterPassword; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getQuestion1() { return question1; }
    public void setQuestion1(String question1) { this.question1 = question1; }

    public String getAnswer1() { return answer1; }
    public void setAnswer1(String answer1) { this.answer1 = answer1; }

    public String getQuestion2() { return question2; }
    public void setQuestion2(String question2) { this.question2 = question2; }

    public String getAnswer2() { return answer2; }
    public void setAnswer2(String answer2) { this.answer2 = answer2; }

    public String getQuestion3() { return question3; }
    public void setQuestion3(String question3) { this.question3 = question3; }

    public String getAnswer3() { return answer3; }
    public void setAnswer3(String answer3) { this.answer3 = answer3; }
}