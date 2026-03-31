package com.flexicharge.flexicharge.security;

import com.flexicharge.flexicharge.model.dto.security.AuthRequest;
import com.flexicharge.flexicharge.model.dto.security.AuthResponse;

public interface AuthService {

    AuthResponse register(AuthRequest request);

    AuthResponse login(AuthRequest request);

}