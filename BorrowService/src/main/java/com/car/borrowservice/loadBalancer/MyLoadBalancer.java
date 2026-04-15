package com.car.borrowservice.loadBalancer;

import com.car.borrowservice.config.BorrowLoadBalancerExcludePortsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 排除若干端口后，在剩余实例中随机选取；排除集合来自 {@link BorrowLoadBalancerExcludePortsHolder}（支持动态刷新）。
 */
public class MyLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Logger log = LoggerFactory.getLogger(MyLoadBalancer.class);

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;
    private final BorrowLoadBalancerExcludePortsHolder excludePortsHolder;

    public MyLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            String serviceId,
            BorrowLoadBalancerExcludePortsHolder excludePortsHolder) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
        this.excludePortsHolder = excludePortsHolder;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable();
        if (supplier == null) {
            return Mono.just(new EmptyResponse());
        }
        return supplier.get(request).next().map(this::pickInstance);
    }

    private Response<ServiceInstance> pickInstance(List<ServiceInstance> instances) {
        Set<Integer> excludePorts = excludePortsHolder.getExcludePorts();

        if (instances == null || instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        List<ServiceInstance> candidates = instances;
        if (!excludePorts.isEmpty()) {
            candidates = instances.stream()
                    .filter(si -> !excludePorts.contains(si.getPort()))
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers left after excluding ports " + excludePorts + " for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        int index = ThreadLocalRandom.current().nextInt(candidates.size());
        return new DefaultResponse(candidates.get(index));
    }
}
