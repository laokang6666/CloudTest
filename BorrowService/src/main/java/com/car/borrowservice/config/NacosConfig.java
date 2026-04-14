package com.car.borrowservice.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.enabled", havingValue = "true", matchIfMissing = true)
public class NacosConfig {

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosAddr;

    @Value("${spring.cloud.nacos.username}")
    private String nacosUser;

    @Value("${spring.cloud.nacos.password}")
    private String nacosPassword;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    @Bean
    public NamingService getNamingEvent(){
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,nacosAddr);
        properties.setProperty(PropertyKeyConst.USERNAME,nacosUser);
        properties.setProperty(PropertyKeyConst.PASSWORD,nacosPassword);
        properties.setProperty(PropertyKeyConst.NAMESPACE,namespace);
        try {
            return NacosFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
