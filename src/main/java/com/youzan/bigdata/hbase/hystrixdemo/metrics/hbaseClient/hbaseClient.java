/**
 * Bilibili.com Inc.
 * Copyright (c) 2009-2019 All Rights Reserved.
 */
package com.youzan.bigdata.hbase.hystrixdemo.metrics.hbaseClient;

/**
 *
 * @author zhouxiaogang
 * @version $Id: hbaseClient.java, v 0.1 2019-09-20 11:45
zhouxiaogang Exp $$
 */
public class hbaseClient {
    public String name;

    public hbaseClient(String zkName){
        this.name = zkName;
    }

    public String toString(){
        return String.format("%s:%s",System.identityHashCode(this),name);
    }

    public static void main(String[] args){
        hbaseClient cli = new hbaseClient("12");

        System.err.println(cli);
        cli.name = "34";
        System.err.println(cli);
        cli.name = "45";
        System.err.println(cli);
    }
}