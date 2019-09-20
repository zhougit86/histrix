/**
 * Bilibili.com Inc.
 * Copyright (c) 2009-2019 All Rights Reserved.
 */
package com.youzan.bigdata.hbase.hystrixdemo.metrics.testcommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 *
 * @author zhouxiaogang
 * @version $Id: TestCommand.java, v 0.1 2019-09-20 16:34
zhouxiaogang Exp $$
 */
public class TestCommand extends HystrixCommand<String>{

    int id;

    public TestCommand(int id) {
        super(HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand"))
//                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("haha"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                        .withCoreSize(30)    //   定义了线程池的大小    netflix 大部分设置为10，极小一部分设为25
//                        .withMetricsRollingStatisticalWindowBuckets(5)
//                        .withAllowMaximumSizeToDivergeFromCoreSize()
                )
                .andCommandPropertiesDefaults(
                        // we want to default to semaphore-isolation since this wraps
                        // 2 others commands that are already thread isolated
                        HystrixCommandProperties.Setter()
//                                .withMetricsHealthSnapshotIntervalInMilliseconds(2000)    //定义多久来计算一次失败率
//                                .withMetricsRollingStatisticalWindowInMilliseconds(5000)   //定义滑动平均的窗口多长，和withMetricsRollingStatisticalWindowBuckets  一对
//                                .withMetricsRollingPercentileEnabled(false)

//                                .withFallbackEnabled(false)     //是否启用fallback机制，当为false的时候，fallback就不会调用了

                                .withExecutionTimeoutInMilliseconds(100)      //定义请求多久超时
//                                .withExecutionTimeoutEnabled(false)

                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)   //因为semaphore不受线程池控制，所以要靠专门参数控制

                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withCircuitBreakerEnabled(false)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerErrorThresholdPercentage(15)//(1)错误百分比超过5%
                                .withCircuitBreakerRequestVolumeThreshold(150)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));
        this.id = id;
    }

    protected String run() throws Exception {
//        Thread.sleep(200);
        throw new Exception("j");
//        return "haha";
    }


    @Override
    protected String getFallback() {

        //当断路器没有断开的时候，使用的是hystrix的线程，
        //当断开时候，直接调用的调用者的线程
        System.out.println(Thread.currentThread());

//        System.out.println("falling back");
        return "oh, yeah.";
    }
}