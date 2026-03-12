package com.revpasswordmanager.serviceimpl;

import com.revpasswordmanager.dto.UserResponseDTO;
import com.revpasswordmanager.entity.User;
import com.revpasswordmanager.repository.UserRepository;
import com.revpasswordmanager.service.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------------- GET ALL USERS ----------------

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- GET USER BY ID ----------------

    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

    // ---------------- GET USER BY USERNAME ----------------

    @Override
    public UserResponseDTO getUserByName(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

    // ---------------- CREATE USER ----------------

    @Override
    public UserResponseDTO createUser(User user) {

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    // ---------------- UPDATE USER ----------------

    @Override
    public UserResponseDTO updateUserById(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

        User savedUser = userRepository.save(existingUser);

        return convertToDTO(savedUser);
    }

    // ---------------- DELETE USER ----------------

    @Override
    public void deleteUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    // ---------------- DTO CONVERSION ----------------

    private UserResponseDTO convertToDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAccountStatus().name(),
                user.getTwoFactorEnabled() != null ? user.getTwoFactorEnabled() : false  // ✅ FIXED
        );
    }

    // ---------------- SPRING SECURITY LOGIN ----------------

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    
    @Override
    public void toggleTwoFactor(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTwoFactorEnabled(!user.getTwoFactorEnabled());

        userRepository.save(user);
    }
}