package com.youzan.bigdata.hbase.hystrixdemo.metrics;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixEventType;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MetricsMain {

    private static String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append("---").append(health.getErrorPercentage()).append("%   ");
            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
            m.append(":::").append(metrics.getCumulativeCount(HystrixEventType.SHORT_CIRCUITED));
//            m.append(":::").append(metrics.get);
//            m.append(":::").append(metrics.getCommandKey());
        }
        return m.toString();
    }

    public static void main(String[] args) {

        ExecutorService es =  Executors.newFixedThreadPool(5);

        Thread checker = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    try{
                        HystrixCommandKey keyName = HystrixCommandKey.Factory.
                                asKey(RandomCommand.class.getSimpleName());
                        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(
                                HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand")

                        );
                        System.out.println( keyName + ":metrics:" + (  metrics == null ? "not initialized" : getStatsStringFromMetrics(metrics)));

                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    try {
//                        Class<?> clazz = Class.forName(HystrixCommandMetrics.class.getName());
//                        Method resetMethod = clazz.getDeclaredMethod("reset", null);
//                        resetMethod.setAccessible(true);
//                        resetMethod.invoke(null);
//                    } catch (Throwable t) {
//                        System.out.println(t.toString());
//                        t.printStackTrace();
//                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ioe) {
                        // ignore
                    } finally {

                    }

                }
            }
        });
        checker.setDaemon(true);
        checker.start();

        for (int i = 0; i < 10000; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    new RandomCommand().execute();
                }
            });

            try {
                Thread.sleep(10);
            } catch (Exception ioe) {
                // ignore
            }
        }


    }
}
