package com.car.borrowservice.runner;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ConditionalOnBean(NamingService.class)
public class NacosInstanceListenRunner implements CommandLineRunner {

    private NamingService namingService;

    public NacosInstanceListenRunner(NamingService namingService){
        this.namingService = namingService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("监听服务运行");

        namingService.subscribe("book-service", "DEFAULT_GROUP",
                (EventListener) event -> {
                    if (event instanceof NamingEvent) {
                        AtomicInteger bookActive = new AtomicInteger();
                        List<Instance> instances = ((NamingEvent) event).getInstances();
                        if (instances.isEmpty()){
                            System.out.println("book 服务全不可用");
                            return;
                        }
                        instances.forEach(
                                instance -> {
                                    if (instance.isEnabled() && instance.isHealthy()){
                                        bookActive.getAndIncrement();
                                    }
                                }
                        );
                        int bookNotActive = instances.size() - bookActive.get();
                        System.out.println("book可用服务：" + bookActive.get() + ";不可用服务:" + bookNotActive);
                    }
                });

        namingService.subscribe("user-service","DEFAULT_GROUP",
                (EventListener) event -> {
                    if (event instanceof NamingEvent) {
                        AtomicInteger userActive = new AtomicInteger();
                        List<Instance> instances = ((NamingEvent) event).getInstances();
                        if (instances.isEmpty()){
                            System.out.println("user 服务全不可用");
                            return;
                        }
                        instances.forEach(
                                instance -> {
                                    if (instance.isEnabled() && instance.isHealthy()){
                                        userActive.getAndIncrement();
                                    }
                                }
                        );
                        int userNotActive = instances.size() - userActive.get();
                        System.out.println("user可用服务：" + userActive.get() + ";不可用服务:" + userNotActive);
                    }
                });

    }
}
