package com.youzan.bigdata.hbase.hystrixdemo.metrics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;


public class RandomCommand extends HystrixCommand<String> {
    public RandomCommand() {
//        super(Setter.withGroupKey().andCommandKey().);
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(20)
                )
                .andCommandPropertiesDefaults(
                        // we want to default to semaphore-isolation since this wraps
                        // 2 others commands that are already thread isolated
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
//                                .withCircuitBreakerEnabled(true)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerErrorThresholdPercentage(15)//(1)错误百分比超过5%
                                .withCircuitBreakerRequestVolumeThreshold(150)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));
    }

    protected String run() throws Exception {
        double ranNum = Math.random();
//        System.err.println(ranNum);
//        if(ranNum>0.9)
        Thread.sleep(700);
        if(ranNum > 0.91) {
            throw new Exception("run");
        }

        return "yeah.";
    }

    @Override
    protected String getFallback() {
//        System.out.println("falling back");
        return "oh, yeah.";
    }
}
