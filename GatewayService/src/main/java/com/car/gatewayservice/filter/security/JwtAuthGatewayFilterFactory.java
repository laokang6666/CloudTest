package com.car.gatewayservice.filter.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 路由级 JWT 校验：仅在配置了 {@code filters: - JwtAuth}（或 {@code name: JwtAuth}）的路由上生效。
 * <p>
 * 要求请求头 {@code Authorization: Bearer &lt;token&gt;}，与 user-service 使用同一 {@code user.jwt.secret} 验签；
 * 成功后向下游追加 {@code X-Gateway-User-Login}（JWT subject）。
 */
@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final byte[] UNAUTH_JSON =
            "{\"code\":\"UNAUTHORIZED\",\"message\":\"Missing or invalid token\"}".getBytes(StandardCharsets.UTF_8);

    private final JwtSigningKeyHolder signingKeyHolder;

    public JwtAuthGatewayFilterFactory(JwtSigningKeyHolder signingKeyHolder) {
        super(Config.class);
        this.signingKeyHolder = signingKeyHolder;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth == null || auth.length() <= BEARER_PREFIX.length() || !auth.startsWith(BEARER_PREFIX)) {
                return unauthorized(exchange);
            }
            String token = auth.substring(BEARER_PREFIX.length()).trim();
            if (token.isEmpty()) {
                return unauthorized(exchange);
            }
            try {
                var jwt = Jwts.parser()
                        .verifyWith(signingKeyHolder.signingKey())
                        .build()
                        .parseSignedClaims(token);
                String subject = jwt.getPayload().getSubject();
                // 将用户信息写入新请求头，并传递给下游
                if (subject != null && !subject.isBlank()) {
                    ServerHttpRequest mutated = request.mutate().header("X-Gateway-User-Login", subject).build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                }
                return chain.filter(exchange);
            } catch (JwtException e) {
                return unauthorized(exchange);
            }
        };
    }

    private static Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(UNAUTH_JSON);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /** 占位配置，便于后续在 yml 中扩展参数。 */
    public static class Config {
    }
}
