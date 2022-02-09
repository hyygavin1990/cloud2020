package com.hyy.springcloud.controller;

import com.hyy.springcloud.entities.CommonResult;
import com.hyy.springcloud.entities.Payment;
import com.hyy.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * (Payment)表控制层
 *
 * @author lixiaolong
 * @date 2022/01/13 16:05
 */
@RestController
@Slf4j
@RequestMapping("payment")
public class PaymentController {

    @Resource
    private PaymentService paymentService;
    @Value("${server.port}")
    private String serverPort;
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("create")
    public CommonResult<Payment> create(@RequestBody Payment payment) {
        Payment insert = paymentService.insert(payment);
        log.info("********** 插入成功 ：" + insert +" ,服务端口号："+ serverPort);
        return new CommonResult<>(200, "insert success", insert);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("get/{id}")
    public CommonResult<Payment> selectOne(@PathVariable("id") Long id) {
        Payment payment = this.paymentService.queryById(id);
        return new CommonResult<>(200, "select success， 服务端口号："+ serverPort, payment);
    }


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("discovery")
    public CommonResult discovery() {
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("************* service= " +service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info("serviceId = {},host = {},port = {},uri = {}",instance.getServiceId(),instance.getHost(),instance.getPort(),instance.getUri());
        }
        return new CommonResult(200,"success",discoveryClient);
    }

    @GetMapping("lb")
    public String lb() {
        return serverPort;
    }

    @GetMapping("feign/timeout")
    public String paymentFeignTimeout() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

}