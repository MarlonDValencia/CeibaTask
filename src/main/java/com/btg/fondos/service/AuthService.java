package com.btg.fondos.service;

import com.btg.fondos.dto.request.LoginRequest;
import com.btg.fondos.dto.request.RegisterRequest;
import com.btg.fondos.dto.response.AuthResponse;
import com.btg.fondos.model.Client;
import com.btg.fondos.repository.ClientRepository;
import com.btg.fondos.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${fund.initial-balance}")
    private double initialBalance;

    public AuthResponse register(RegisterRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }

        Client client = Client.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .balance(initialBalance)
                .notificationPreference(request.getNotificationPreference())
                .build();

        client = clientRepository.save(client);

        String token = jwtTokenProvider.generateToken(
                client.getId(), client.getEmail(), client.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .clientId(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Client client = clientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(
                client.getId(), client.getEmail(), client.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .clientId(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .build();
    }
}
