package com.car.borrowservice.config;

import com.car.borrowservice.loadBalancer.MyLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class MyCustomLoadBalancerConfiguration {

    /**
     * 排除端口由 {@link BorrowLoadBalancerExcludePortsHolder}（{@code @RefreshScope} 与 {@code @Value}）提供，支持动态刷新。
     */
    @Bean
    ReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            BorrowLoadBalancerExcludePortsHolder excludePortsHolder) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new MyLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name,
                excludePortsHolder);
    }
}
