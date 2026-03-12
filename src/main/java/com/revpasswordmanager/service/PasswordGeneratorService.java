package com.revpasswordmanager.service;

import com.revpasswordmanager.dto.PasswordGeneratorRequestDTO;
import java.util.List;

public interface PasswordGeneratorService {

    String generatePassword(PasswordGeneratorRequestDTO dto);

    List<String> generateMultiplePasswords(PasswordGeneratorRequestDTO dto, int count);

    String checkStrength(String password);
}