package com.youzan.bigdata.hbase.hystrixdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.youzan.bigdata.hbase.hystrixdemo.hbase.impl.HystrixHBaseItemStorage;
import com.youzan.bigdata.hbase.hystrixdemo.hbase.impl.Item;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Configuration mConfiguration = HBaseConfiguration.create();
        mConfiguration.set("hbase.zookeeper.property.clientPort", "2181");
        mConfiguration.set("hbase.zookeeper.quorum", "172.22.33.191,172.22.33.91,172.22.33.88");
        mConfiguration.set("hbase.master", "172.22.33.191:16000");

        Configuration sConfiguration = HBaseConfiguration.create();
        sConfiguration.set("hbase.zookeeper.property.clientPort", "2181");
        sConfiguration.set("hbase.zookeeper.quorum", "172.22.33.94,172.22.33.99,172.22.33.97");
        sConfiguration.set("hbase.master", "172.22.33.91:16000");


        HystrixHBaseItemStorage hhs = new HystrixHBaseItemStorage(mConfiguration,sConfiguration);
        hhs.init();
        Item myItem =  hhs.getById("xuezhaoming-0");
        System.err.println(myItem);

        System.out.println( "Hello World!" );
    }
}
