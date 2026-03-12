package com.revpasswordmanager.service;

import java.util.List;

import com.revpasswordmanager.dto.UserResponseDTO;
import com.revpasswordmanager.entity.User;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByName(String username);

    UserResponseDTO createUser(User user);

    UserResponseDTO updateUserById(Long id, User updatedUser);

    void deleteUserById(Long id);

	void toggleTwoFactor(Long id);
}