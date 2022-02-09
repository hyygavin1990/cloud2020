package com.hyy.springcloud.lb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lixiaolong
 * @date 2020/12/23 9:25
 * @description 自定义的负载均衡实现
 */
@Component
@Slf4j
public class MyLB implements MyLoadBalancer {
    // 原子类
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * @author lixiaolong
     * @date 2020/12/23 10:07
     * @description 判断时第几次访问
     */
    public final int getAndIncrement() {
        int current;
        String a = "current";
        int next = 0;
        do {
            current = atomicInteger.get();
            // 防止越界
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        } while (!atomicInteger.compareAndSet(current, next));
        log.info("*****第{}次访问*****" , next);
        return next;
    }

    /**
     * 负载均衡算法：rest接口第几次请求数 % 服务器集群总数量 = 实际调用服务器位置下标， 每次服务重启动后rest接口计数从1开始。1
     *
     * @param serviceInstances
     * @return ServiceInstance
     * @author lixiaolong
     * @date 2020/12/23 9:51
     */
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstances) {
        int index = getAndIncrement() % serviceInstances.size();

        return serviceInstances.get(index);
    }

}
