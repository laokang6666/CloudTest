package com.car.borrowservice.runner;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Profile("!test")
public class NacosListenRunner implements ApplicationRunner {

    private final NacosConfigManager nacosConfigManager;

    public NacosListenRunner(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("监听配置运行");
        ConfigService configService = nacosConfigManager.getConfigService();
        configService.addListener("borrow-service-dev.yaml", "DEFAULT_GROUP", new Listener() {
            @Override
            public Executor getExecutor() {
                ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(5);
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,4,60, TimeUnit.SECONDS,
                        blockingQueue, Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
                return threadPoolExecutor;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("配置变化");
                System.out.println("发送邮件");
            }
        });
    }
}
