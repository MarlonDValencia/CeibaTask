package com.btg.fondos.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "c2VjdXJlLWtleS1mb3ItYnRnLXBhY3R1YWwtZm9uZG9zLWFwcGxpY2F0aW9uLTIwMjU=";
        jwtTokenProvider = new JwtTokenProvider(secret, 86400000);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtTokenProvider.generateToken("client-1", "test@email.com", "CLIENT");

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void getClientIdFromToken_shouldReturnCorrectId() {
        String token = jwtTokenProvider.generateToken("client-1", "test@email.com", "CLIENT");

        String clientId = jwtTokenProvider.getClientIdFromToken(token);

        assertThat(clientId).isEqualTo("client-1");
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertThat(jwtTokenProvider.validateToken("invalid-token")).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(
                "c2VjdXJlLWtleS1mb3ItYnRnLXBhY3R1YWwtZm9uZG9zLWFwcGxpY2F0aW9uLTIwMjU=", 0);

        String token = shortLivedProvider.generateToken("client-1", "test@email.com", "CLIENT");

        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
