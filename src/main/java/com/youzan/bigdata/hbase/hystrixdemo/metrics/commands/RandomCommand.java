package com.youzan.bigdata.hbase.hystrixdemo.metrics.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient;

import java.util.concurrent.ThreadLocalRandom;


public class RandomCommand extends HystrixCommand<HbaseResult> {
    private int id;
    private hbaseClient hbaseClient;
    private boolean useSecond;
    private double failCritial;
    private int usePrimay =10 ;

    private static final ThreadLocalRandom RANDOM =
            ThreadLocalRandom.current();

    public RandomCommand(int id) {
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
                            .withCircuitBreakerEnabled(true)
//                            .withCircuitBreakerErrorThresholdPercentage(50)
                            .withCircuitBreakerErrorThresholdPercentage(35)//(1)错误百分比超过5%
                            .withCircuitBreakerRequestVolumeThreshold(150)//(2)10s以内调用次数10次，同时满足(1)(2)熔断器打开
                ));


        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand")
        );

        useSecond = false;

        //mock for highspeed failure
        long movingCounting = metrics.getHealthCounts().getTotalRequests();

        //=============================

        HystrixCommandMetrics pmetrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimaryCommand")
        );

        //mock for highspeed failure
        long pmovingCounting = pmetrics.getHealthCounts().getTotalRequests();
        long pfailureRate = pmetrics.getHealthCounts().getErrorPercentage();



        if (pmovingCounting<50){
            failCritial = 1;
        }else if(pmovingCounting<90){
            failCritial = 0.96;
        }else{
            failCritial = 0.92;
        }


        if (pfailureRate>=10){
            //十成转移
            usePrimay = 0;
        }else if ( pfailureRate >=5){
            usePrimay = 5;
        }else {
            usePrimay = 10;
        }
        this.id = id;
        this.hbaseClient = new hbaseClient("11");
    }

    private static final boolean usePrimaryByProbable(int probability){
        if(RANDOM.nextInt(10)>(probability-1)){
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        double cu = 0;
        for(int i = 0;i<100000;i++){
            if (usePrimaryByProbable(8)){
                cu++;
            }
        }
        System.err.println(cu/100000);
    }

    protected HbaseResult run() throws Exception {
        HystrixCommandMetrics pmetrics = HystrixCommandMetrics.getInstance(
                HystrixCommandKey.Factory.asKey("PrimaryCommand")
        );

        //mock for highspeed failure
        long pmovingCounting = pmetrics.getHealthCounts().getTotalRequests();

        useSecond = true;

        if (usePrimaryByProbable(usePrimay) || pmovingCounting==0){
            HbaseResult firstResult = new PrimaryCommand(id,hbaseClient,failCritial).execute();
            if (!firstResult.isSuccess()){
                throw new Exception("first attempt fail");
            }
            return firstResult;
        }else {
            return new SecondaryCommand(id,hbaseClient,failCritial).execute();
        }
    }


    @Override
    protected HbaseResult getFallback() {

        if(useSecond)
            return new SecondaryCommand(id,hbaseClient,failCritial).execute();

        if (usePrimaryByProbable(usePrimay)){
            HbaseResult firstResult = new PrimaryCommand(id,hbaseClient,failCritial).execute();
            if (!firstResult.isSuccess()){
                return new SecondaryCommand(id,hbaseClient,failCritial).execute();
            }
            return firstResult;
        }else {
            return new SecondaryCommand(id,hbaseClient,failCritial).execute();
        }
    }
}
