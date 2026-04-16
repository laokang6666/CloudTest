package com.car.gatewayservice.filter.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局访问日志：请求进入打一条，响应结束打一条（状态码与耗时），不记录 Authorization 内容。
 */
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AccessLogGlobalFilter.class);
    private static final String ATTR_START_NS = AccessLogGlobalFilter.class.getName() + ".startNs";

    private final boolean enabled;

    public AccessLogGlobalFilter(@Value("${gateway.access-log.enabled:true}") boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }
        exchange.getAttributes().put(ATTR_START_NS, System.nanoTime());

        ServerHttpRequest req = exchange.getRequest();
        String method = req.getMethod().name();
        String path = req.getURI().getPath();
        String client = req.getHeaders().getFirst("X-Forwarded-For");
        if (client == null || client.isBlank()) {
            client = req.getRemoteAddress() != null ? req.getRemoteAddress().getAddress().getHostAddress() : "-";
        }

        if (log.isInfoEnabled()) {
            log.info("gateway request method={} path={} client={}", method, path, client);
        }

        return chain.filter(exchange).doFinally(signalType -> {
            Long startNs = exchange.getAttribute(ATTR_START_NS);
            long durationMs = startNs == null ? -1L : (System.nanoTime() - startNs) / 1_000_000L;
            if (log.isInfoEnabled()) {
                log.info(
                        "gateway response method={} path={} status={} durationMs={}",
                        method,
                        path,
                        exchange.getResponse().getStatusCode(),
                        durationMs);
            }
        });
    }

    @Override
    public int getOrder() {
        return -800;
    }
}
