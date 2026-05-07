package com.fuerzapp.service;

import com.fuerzapp.dto.LoginRequest;
import com.fuerzapp.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
