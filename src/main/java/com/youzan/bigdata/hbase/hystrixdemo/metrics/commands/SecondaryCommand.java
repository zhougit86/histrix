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
 * @version $Id: SecondaryCommand.java, v 0.1 2019-09-20 12:02
zhouxiaogang Exp $$
 */
public class SecondaryCommand extends HystrixCommand<HbaseResult> {

    private final int id;
    private com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient hbaseClient;
    private double critial;

    public SecondaryCommand(int id,hbaseClient hbaseClient,double critial) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SecondaryCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SecondaryCommand"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(30)    //   定义了线程池的大小    netflix 大部分设置为10，极小一部分设为25
                )
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(200)
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
//                        .withCircuitBreakerEnabled(false)
                )
        );
        this.id = id;
        this.hbaseClient = hbaseClient;
        this.critial = critial;
    }

    @Override
    protected HbaseResult run() {
        // perform fast 'secondary' service call
        return new HbaseResult<Integer>(true,id);
    }

}