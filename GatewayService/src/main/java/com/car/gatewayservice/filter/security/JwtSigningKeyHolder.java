package com.car.gatewayservice.filter.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/** 与 user-service {@code JwtTokenService} 相同的 HS256 密钥构造规则。 */
@Component
public class JwtSigningKeyHolder {

    private final SecretKey signingKey;

    public JwtSigningKeyHolder(
            @Value("${user.jwt.secret}") String rawSecret) {
        byte[] keyBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("user.jwt.secret must be at least 32 bytes for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public SecretKey signingKey() {
        return signingKey;
    }
}
