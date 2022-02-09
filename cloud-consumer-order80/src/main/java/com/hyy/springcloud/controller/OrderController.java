package com.hyy.springcloud.controller;

import com.hyy.springcloud.entities.CommonResult;
import com.hyy.springcloud.entities.Payment;
import com.hyy.springcloud.lb.MyLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@RestController
@Slf4j
public class OrderController {

    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/consumer/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        CommonResult result = restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommonResult.class);
        if(result!=null&&result.getCode()==200&&result.getData()!=null){
            log.info("********** consumer端 调用成功，插入数据 ：" + result.getData());
        }
        return result;
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> get(@PathVariable("id") Long id){
        CommonResult result = restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
        if(result!=null&&result.getCode()==200&&result.getData()!=null){
            log.info("********** consumer端 调用成功，查询数据 ：" + result.getData());
        }
        return result;
    }

    /**
     * MySelfRule 修改默认的负载均衡配置
     * @return
     */
//    @GetMapping("/consumer/payment/lb")
//    public String lb(){
//        String result = restTemplate.getForObject(PAYMENT_URL+"/payment/lb",String.class);
//        if(StringUtils.isNotBlank(result)){
//            log.info("********** consumer端 调用成功，查询数据 ：" + result);
//        }
//        return result;
//    }


    // 注入自定义的负载均衡规则
    @Resource
    private MyLoadBalancer myLoadBalancer;

    @Resource
    private DiscoveryClient discoveryClient;
    /**
     * 测试自定义轮询负载均衡规则
     */
    @GetMapping(value = "/consumer/payment/lb")
    public String getPaymentLB() {
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");

        if (instances == null || instances.isEmpty()) {
            return null;
        }

        // 调用自定义的负载均衡策略
        ServiceInstance serviceInstance = myLoadBalancer.instances(instances);
        URI uri = serviceInstance.getUri();
        return restTemplate.getForObject(uri + "/payment/lb", String.class);

    }

}
