package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.User;
import com.flexicharge.flexicharge.model.dto.security.AuthRequest;
import com.flexicharge.flexicharge.model.dto.security.AuthResponse;
import com.flexicharge.flexicharge.repository.UserRepository;
import com.flexicharge.flexicharge.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request) {
        User user = new User();
        user.setUsername(request.username());
        // ENCRIPTAMOS la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of("ROLE_USER"));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        // El AuthenticationManager verifica si el user/pass es correcto
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = jwtService.generateToken(request.username());
        return new AuthResponse(token);
    }
}