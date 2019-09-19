package com.youzan.bigdata.hbase.hystrixdemo.metrics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;


public class RandomCommand extends HystrixCommand<String> {
    private int id;

    public RandomCommand(int id) {
//        super(Setter.withGroupKey().andCommandKey().);
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                )
                .andCommandPropertiesDefaults(
                        // we want to default to semaphore-isolation since this wraps
                        // 2 others commands that are already thread isolated
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withCircuitBreakerEnabled(false)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerErrorThresholdPercentage(15)//(1)错误百分比超过5%
                                .withCircuitBreakerRequestVolumeThreshold(150)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));
        this.id = id;
    }

    protected String run() throws Exception {
        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand")
        );

        //mock for highspeed failure
        long movingCounting = metrics.getHealthCounts().getTotalRequests();
        long failureRate = metrics.getHealthCounts().getErrorPercentage();

        double failCritial = 1;
        if (movingCounting<300){
            failCritial = 1;
        }else if(movingCounting<500){
            failCritial = 0.8;
        }else{
            failCritial = 0.6;
        }

        int migrateRatio = 0;
        if (failureRate>=35){
            //十成转移
            migrateRatio = 10;
        }else if ( failureRate >=15){
            migrateRatio = 5;
        }else {
            migrateRatio = 1;
        }

        if (id%10 >=migrateRatio){
            return actualBehave(failCritial);
        }else {
            return "transfered";
        }


    }

    protected String actualBehave(double critial )throws Exception{
        double ranNum = Math.random();
//        System.err.println(ranNum);
//        if(ranNum>0.9)
        if(ranNum > critial) {
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
