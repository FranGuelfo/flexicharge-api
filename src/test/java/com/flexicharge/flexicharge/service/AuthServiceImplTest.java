package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.User;
import com.flexicharge.flexicharge.model.dto.security.AuthRequest;
import com.flexicharge.flexicharge.model.dto.security.AuthResponse;
import com.flexicharge.flexicharge.repository.UserRepository;
import com.flexicharge.flexicharge.security.JwtService;
import com.flexicharge.flexicharge.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthServiceImpl authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest("fran", "password123");
    }

    @Test
    void register_ShouldSaveUserAndReturnToken() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.generateToken(anyString())).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = authService.register(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        // Arrange
        when(jwtService.generateToken(anyString())).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
