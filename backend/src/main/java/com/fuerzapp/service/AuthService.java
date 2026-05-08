package com.fuerzapp.service;

import com.fuerzapp.dto.CambiarPasswordRequest;
import com.fuerzapp.dto.LoginRequest;
import com.fuerzapp.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void cambiarPassword(CambiarPasswordRequest request);
}
