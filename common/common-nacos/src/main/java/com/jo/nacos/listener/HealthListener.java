package com.jo.nacos.listener;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.AbstractEventListener;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

/**
 * @author Jo
 * @date 2024/7/24
 */
@AutoConfiguration
public class HealthListener {
    @Value("${spring.application.name}")
    String serviceName;

    @PostConstruct
    public void listen() throws NacosException {

        String addr = "192.168.50.6:8848";

        Properties properties = new Properties();

        properties.put("serverAddr",addr);

        NamingService namingService = NacosFactory.createNamingService(properties);

        namingService.subscribe(serviceName, new AbstractEventListener() {
            @Override
            public void onEvent(Event event) {
                // 当服务列表发生变化时，会调用此方法
                System.out.println("--------------------");
                try {
                    // 获取最新的服务列表
                    List<Instance> instances = namingService.selectInstances(serviceName, true);
                    for (Instance instance : instances) {
                        System.out.println("Instance: " + instance.getIp() + ":" + instance.getPort() + " is " + (instance.isHealthy() ? "healthy" : "unhealthy"));
                    }
                } catch (NacosException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}
