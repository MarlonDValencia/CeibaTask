package com.btg.fondos.service;

import com.btg.fondos.dto.request.LoginRequest;
import com.btg.fondos.dto.request.RegisterRequest;
import com.btg.fondos.dto.response.AuthResponse;
import com.btg.fondos.enums.NotificationType;
import com.btg.fondos.model.Client;
import com.btg.fondos.repository.ClientRepository;
import com.btg.fondos.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateClientAndReturnToken() {
        RegisterRequest request = new RegisterRequest(
                "Test User", "test@email.com", "3001234567", "password123", NotificationType.EMAIL);

        Client savedClient = Client.builder()
                .id("client-1")
                .name("Test User")
                .email("test@email.com")
                .build();

        when(clientRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void register_shouldThrowWhenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "Test User", "test@email.com", "3001234567", "password123", NotificationType.EMAIL);

        when(clientRepository.existsByEmail("test@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email ya se encuentra registrado");
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        LoginRequest request = new LoginRequest("test@email.com", "password123");

        Client client = Client.builder()
                .id("client-1")
                .name("Test User")
                .email("test@email.com")
                .password("encoded-password")
                .build();

        when(clientRepository.findByEmail("test@email.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldThrowWhenEmailNotFound() {
        LoginRequest request = new LoginRequest("unknown@email.com", "password123");

        when(clientRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_shouldThrowWhenPasswordInvalid() {
        LoginRequest request = new LoginRequest("test@email.com", "wrong-password");

        Client client = Client.builder()
                .id("client-1")
                .email("test@email.com")
                .password("encoded-password")
                .build();

        when(clientRepository.findByEmail("test@email.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
