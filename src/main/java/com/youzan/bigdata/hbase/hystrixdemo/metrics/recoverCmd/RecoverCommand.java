/**
 * Bilibili.com Inc.
 * Copyright (c) 2009-2019 All Rights Reserved.
 */
package com.youzan.bigdata.hbase.hystrixdemo.metrics.recoverCmd;

import com.netflix.hystrix.*;

import java.util.Calendar;

/**
 *
 * @author zhouxiaogang
 * @version $Id: TestCommand.java, v 0.1 2019-09-20 16:34
zhouxiaogang Exp $$
 */
public class RecoverCommand extends HystrixCommand<String>{

    int id;

    public RecoverCommand(int id) {
        super(Setter
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

                                .withExecutionTimeoutInMilliseconds(300)      //定义请求多久超时
//                                .withExecutionTimeoutEnabled(false)

//                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)   //因为semaphore不受线程池控制，所以要靠专门参数控制

                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withCircuitBreakerEnabled(true)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerErrorThresholdPercentage(1)//(1)错误百分比超过5%
                                .withCircuitBreakerRequestVolumeThreshold(1)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));
        this.id = id;
    }

    protected String run() throws Exception {
        String word = String.format("success %d,%s",id, Calendar.getInstance().getTime());

        System.err.println(word);
        Thread.sleep(700);

        return word;
    }


    @Override
    protected String getFallback() {

        String word = String.format("fallback %d,%s",id, Calendar.getInstance().getTime());

        //当断路器没有断开的时候，使用的是hystrix的线程，
        //当断开时候，直接调用的调用者的线程
        System.err.println(word);

//        System.out.println("falling back");
        return word;
    }
}