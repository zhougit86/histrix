/**
 * Bilibili.com Inc.
 * Copyright (c) 2009-2019 All Rights Reserved.
 */
package com.youzan.bigdata.hbase.hystrixdemo.metrics.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient;

/**
 *
 * @author zhouxiaogang
 * @version $Id: SecondaryCommand.java, v 0.1 2019-09-20 12:02
zhouxiaogang Exp $$
 */
public class SecondaryCommand extends HystrixCommand<String> {

    private final int id;
    private com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient.hbaseClient hbaseClient;
    private double critial;

    public SecondaryCommand(int id,hbaseClient hbaseClient,double critial) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SecondaryCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SecondaryCommand"))
                .andCommandPropertiesDefaults(
                        // we default to a 100ms timeout for secondary
                        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(100)));
        this.id = id;
        this.hbaseClient = hbaseClient;
        this.critial = critial;
    }

    @Override
    protected String run() {
        // perform fast 'secondary' service call
        return "responseFromSecondary-" + id;
    }

}