package com.ardkyer.rion.service;

import com.ardkyer.rion.dto.UserRegistrationDto;
import com.ardkyer.rion.dto.request.LoginRequest;
import com.ardkyer.rion.dto.request.SignupRequest;
import com.ardkyer.rion.dto.response.LoginResponse;
import com.ardkyer.rion.dto.response.UserResponse;
import com.ardkyer.rion.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    // 이메일 인증
    void sendVerificationEmail(String email);
    boolean verifyEmail(String email, String code);
    boolean isEmailVerified(String email);

    // 사용자 등록/로그인
    User registerNewUser(UserRegistrationDto registrationDto);
    User registerUser(User user);
    LoginResponse login(LoginRequest request);
    UserResponse registerUser(SignupRequest request);

    // 사용자 조회
    User findByUsername(String username);
    User findById(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();

    // 사용자 관리
    User updateUser(User user);
    void deleteUser(Long id);
    String updateUserAvatar(String username, MultipartFile file) throws IOException;
    List<User> getTopUsersByFollowerCount(int limit);
}