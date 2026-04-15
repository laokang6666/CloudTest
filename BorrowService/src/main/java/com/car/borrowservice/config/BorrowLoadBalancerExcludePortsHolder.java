package com.car.borrowservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 负载均衡排除端口：通过 {@code @Value} 绑定配置，{@code @RefreshScope} 支持 Nacos 等触发的动态刷新。
 * <p>
 * {@code borrow.loadbalancer.exclude-ports} 为<strong>逗号分隔</strong>端口列表（如 {@code 8082,8084}）；
 * 可与 {@code borrow.loadbalancer.exclude-port} 单正值合并。
 */
@Component
@RefreshScope
public class BorrowLoadBalancerExcludePortsHolder {

    @Value("${borrow.loadbalancer.exclude-ports:}")
    private String excludePortsCsv;

    @Getter
    private volatile Set<Integer> excludePorts = Collections.emptySet();

    @PostConstruct
    public void rebuild() {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        if (excludePortsCsv != null && !excludePortsCsv.isBlank()) {
            for (String part : excludePortsCsv.split(",")) {
                String t = part.trim();
                if (t.isEmpty()) {
                    continue;
                }
                try {
                    int p = Integer.parseInt(t);
                    if (p > 0) {
                        set.add(p);
                    }
                } catch (NumberFormatException ignored) {
                    // 忽略非法片段
                }
            }
        }
        excludePorts = Set.copyOf(set);
    }

}
