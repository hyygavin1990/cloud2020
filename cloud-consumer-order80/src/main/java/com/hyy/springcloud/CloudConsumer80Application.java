package com.hyy.springcloud;

import com.hyy.myrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableEurekaClient
//@RibbonClient(name = "CLOUD-PAYMENT-SERVICE", configuration = MySelfRule.class) // 启动该服务时去加载自定义的ribbon配置
public class CloudConsumer80Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudConsumer80Application.class);
    }
}
