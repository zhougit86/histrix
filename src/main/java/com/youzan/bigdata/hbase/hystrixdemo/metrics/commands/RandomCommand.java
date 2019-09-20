package com.youzan.bigdata.hbase.hystrixdemo.metrics.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient;


public class RandomCommand extends HystrixCommand<String> {
    private int id;
    private hbaseClient hbaseClient;

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
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withCircuitBreakerEnabled(false)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerErrorThresholdPercentage(15)//(1)错误百分比超过5%
                                .withCircuitBreakerRequestVolumeThreshold(150)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));
        this.id = id;
        this.hbaseClient = new hbaseClient("11");
    }

    protected String run() throws Exception {

        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand")
        );

        //mock for highspeed failure
        long movingCounting = metrics.getHealthCounts().getTotalRequests();
        long failureRate = metrics.getHealthCounts().getErrorPercentage();

        //=============================

        HystrixCommandMetrics pmetrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimaryCommand")
        );

        //mock for highspeed failure
        long pmovingCounting = pmetrics.getHealthCounts().getTotalRequests();
        long pfailureRate = pmetrics.getHealthCounts().getErrorPercentage();

        //-------------------------------
        HystrixCommandMetrics smetrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("SecondaryCommand")
        );

        //mock for highspeed failure
        long smovingCounting = smetrics.getHealthCounts().getTotalRequests();
        long sfailureRate = smetrics.getHealthCounts().getErrorPercentage();

        double failCritial = 1;
        if (movingCounting<300){
            failCritial = 1;
        }else if(movingCounting<500){
            failCritial = 0.8;
        }else{
            failCritial = 0.6;
        }

        int migrateRatio = 0;
        if (pfailureRate>=35){
            //十成转移
            migrateRatio = 10;
        }else if ( pfailureRate >=15){
            migrateRatio = 5;
        }else {
            migrateRatio = 1;
        }

        if (id%10 >=migrateRatio){
//            System.err.printf("%d:%d\n",pfailureRate,migrateRatio);
            return new PrimaryCommand(id,hbaseClient,failCritial).execute();
        }else {
            return new SecondaryCommand(id,hbaseClient,failCritial).execute();
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
