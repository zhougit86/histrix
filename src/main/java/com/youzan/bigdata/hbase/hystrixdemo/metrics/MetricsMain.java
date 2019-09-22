package com.youzan.bigdata.hbase.hystrixdemo.metrics;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.commands.PrimaryCommand;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.commands.RandomCommand;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.commands.SecondaryCommand;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.recoverCmd.RecoverCommand;
import com.youzan.bigdata.hbase.hystrixdemo.metrics.testcommand.TestCommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static com.netflix.hystrix.HystrixEventType.THREAD_POOL_REJECTED;


public class MetricsMain {

    private static String getStatsStringFromMetrics(HystrixCommandMetrics metrics
            ,HystrixCommandMetrics pmetrics
            ,HystrixCommandMetrics smetrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            HystrixCommandMetrics.HealthCounts phealth = pmetrics.getHealthCounts();
            HystrixCommandMetrics.HealthCounts shealth = smetrics.getHealthCounts();
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append("---").append(health.getErrorPercentage()).append("%,")
                    .append(metrics.getCumulativeCount(HystrixEventType.SHORT_CIRCUITED)).append(",").append(metrics.getCumulativeCount(THREAD_POOL_REJECTED)).append("     ");
            m.append("pRequests: ").append(phealth.getTotalRequests()).append(" ");
            m.append("pErrors: ").append(phealth.getErrorCount()).append("---").append(phealth.getErrorPercentage()).append("%,")
                    .append(pmetrics.getCumulativeCount(HystrixEventType.SHORT_CIRCUITED)).append(",").append(pmetrics.getCumulativeCount(THREAD_POOL_REJECTED)).append("     ");
            m.append("sRequests: ").append(shealth.getTotalRequests()).append(" ");
            m.append("sErrors: ").append(shealth.getErrorCount()).append("---").append(shealth.getErrorPercentage()).append("%,")
                    .append(smetrics.getCumulativeCount(HystrixEventType.SHORT_CIRCUITED)).append(",").append(smetrics.getCumulativeCount(THREAD_POOL_REJECTED)).append("     ");
//            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
//            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
//            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
//            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
//            m.append(":::").append(pmetrics==null?"":   pmetrics.getHealthCounts().getTotalRequests());
//            m.append(":::").append(smetrics==null?"": smetrics.getHealthCounts().getTotalRequests());
//            m.append(":::").append(metrics.getCumulativeCount(THREAD_POOL_REJECTED));
            m.append(":::").append(phealth.getTotalRequests() + shealth.getTotalRequests() -health.getTotalRequests());
        }
        return m.toString();
    }

    public static void main(String[] args) {
        HystrixPlugins.reset();
//        System.err.println(HystrixPlugins.getInstance().getDynamicProperties());

        ExecutorService es =  Executors.newFixedThreadPool(50);

        Thread checker = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    try{
                        HystrixCommandKey keyName = HystrixCommandKey.Factory.
                                asKey(RandomCommand.class.getSimpleName());
                        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(
                                HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand")
                        );

                        HystrixCommandMetrics pmetrics = HystrixCommandMetrics.getInstance(
                                HystrixCommandKey.Factory.asKey("PrimaryCommand")
                        );

                        HystrixCommandMetrics smetrics = HystrixCommandMetrics.getInstance(
                                HystrixCommandKey.Factory.asKey("SecondaryCommand")
                        );

                        System.out.println( keyName + ":metrics:" + (  metrics == null ? "not initialized" :
                                getStatsStringFromMetrics(metrics,pmetrics,smetrics)));

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

        new PrimaryCommand(0,null,0);
        new SecondaryCommand(0,null,0);

        for (int i = 0; i < 10000; i++) {
            es.submit(new myRun(i));

            try {
                Thread.sleep(100);
            } catch (Exception ioe) {
                // ignore
            }
        }


    }


}

class myRun implements Runnable{
    private int id;
    public myRun(int id){
        this.id = id;
    }

    public void run(){
//        getNextInt()

        new RandomCommand(id).execute();
//        new TestCommand(id).execute();
//        new RecoverCommand(id).execute();
    }
}
