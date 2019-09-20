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
 * @version $Id: PrimaryCommand.java, v 0.1 2019-09-20 11:56
zhouxiaogang Exp $$
 */
public class PrimaryCommand extends HystrixCommand<String> {

    private final int id;
    private hbaseClient hbaseClient;
    private double critial;

    public PrimaryCommand(int id,hbaseClient hbaseClient,double critial) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimaryCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("PrimaryCommand"))
                .andCommandPropertiesDefaults(
                        // we default to a 600ms timeout for primary
                        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(600)));
        this.id = id;
        this.hbaseClient = hbaseClient;
        this.critial = critial;
    }

    @Override
    protected String run() throws Exception{
        double ranNum = Math.random();
//        System.err.println(ranNum);
//        if(ranNum>0.9)
        if(ranNum > critial) {
            throw new Exception("run");
        }



        return "yeah.";
    }

}