package com.youzan.bigdata.hbase.hystrixdemo.metrics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;


public class RandomCommand extends HystrixCommand<String> {
    public RandomCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("TestMetrics"));
    }

    protected String run() throws Exception {
        double ranNum = Math.random();
        if(ranNum>0.7)
        Thread.sleep(700);
        if(ranNum > 0.9) {
            throw new Exception("run");
        }

        return "yeah.";
    }

    @Override
    protected String getFallback() {
        return "oh, yeah.";
    }
}
