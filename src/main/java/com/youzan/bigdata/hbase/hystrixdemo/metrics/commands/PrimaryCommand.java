/**
 * Bilibili.com Inc.
 * Copyright (c) 2009-2019 All Rights Reserved.
 */
package com.youzan.bigdata.hbase.hystrixdemo.metrics.commands;

import com.netflix.hystrix.*;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient;

/**
 *
 * @author zhouxiaogang
 * @version $Id: PrimaryCommand.java, v 0.1 2019-09-20 11:56
zhouxiaogang Exp $$
 */
public class PrimaryCommand extends HystrixCommand<HbaseResult> {

    private final int id;
    private hbaseClient hbaseClient;
    private double critial;

    public PrimaryCommand(int id,hbaseClient hbaseClient,double critial) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimaryCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("PrimaryCommand"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(30)    //   定义了线程池的大小    netflix 大部分设置为10，极小一部分设为25
                )
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(600)
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
//                                .withCircuitBreakerEnabled(false)
                )
        );
        this.id = id;
        this.hbaseClient = hbaseClient;
        this.critial = critial;
    }

    @Override
    protected HbaseResult run() throws Exception{
        //mock code, analog for the failure
        double ranNum = Math.random();
        if(ranNum > critial) {
            throw new Exception("run");
        }

        return new HbaseResult<Integer>(true,id);
    }

    @Override
    protected HbaseResult getFallback() {
        return new HbaseResult<Integer>(false,id);
    }

}