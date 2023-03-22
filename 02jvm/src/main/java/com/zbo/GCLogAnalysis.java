package com.zbo;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 演示GC日志生成与解读
 */
public class GCLogAnalysis {
    private static Random random = new Random();

    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());


        /**
         * ==============操作步骤（代码没有包路径）==============
         * 备注：
         *      代码中不存在package xxx;类似的代码。即代码文件是放在/src/java/路径里面的
         *      在用java命令执行class文件的时候，也不能指定包。否则会提示‘错误: 找不到或无法加载主类 GCLogAnalysis’
         * 进入代码文件目录
         *      >cd E:\ideaProjects\learn_jksj\02jvm\src\main\java
         * 编译
         *      >javac -encoding utf-8 GCLogAnalysis.java
         * 用java命令执行class文件 & 打印gc日志
         *      >java -XX:+PrintGCDetails GCLogAnalysis
         *      >java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
         *
         *
         *
         * ==============操作步骤（代码有包路径）==============
         * 备注：
         *      代码中存在package com.zbo;类似的代码。即代码文件是放在/src/java/com/zbo/路径里面的
         *      在用java命令执行class文件的时候，需要指定包。并在包的上层执行（即/src/java/）。否则会提示‘错误: 找不到或无法加载主类 com.zbo.GCLogAnalysis’
         * 进入代码文件目录
         *      >cd E:\ideaProjects\learn_jksj\02jvm\src\main\java\com\zbo
         * 编译
         *      >javac -encoding utf-8 GCLogAnalysis.java
         * 再回到src/main目录
         *      >cd E:\ideaProjects\learn_jksj\02jvm\src\main
         * 用java命令执行class文件 & 打印gc日志
         *      >java -XX:+PrintGCDetails com.zbo.GCLogAnalysis
         *      >java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      模拟相同gc 不同堆内存
         *      >java -Xmx128m -Xloggc:gc.128m.log -XX:+PrintGCDetails com.zbo.GCLogAnalysis                                        改小内存模拟OOM
         *      >java -Xmx512m -Xloggc:gc.512m.log -XX:+PrintGCDetails com.zbo.GCLogAnalysis
         *      >java -Xmx1g -Xloggc:gc.1g.log -XX:+PrintGCDetails com.zbo.GCLogAnalysis
         *      >java -Xmx2g -Xloggc:gc.2g.log -XX:+PrintGCDetails com.zbo.GCLogAnalysis
         *      >java -Xmx4g -Xloggc:gc.4g.log -XX:+PrintGCDetails com.zbo.GCLogAnalysis
         *      模拟不同gc 相同的512m堆内存
         *      >java -XX:+UseSerialGC -Xms512m -Xmx512m -Xloggc:gc.serialGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseParallelGC -Xms512m -Xmx512m -Xloggc:gc.parallelGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -Xloggc:gc.concMarkSweepGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseG1GC -Xms512m -Xmx512m -Xloggc:gc.g1GC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      模拟不同gc 相同的4g堆内存
         *      >java -XX:+UseSerialGC -Xms4g -Xmx4g -Xloggc:gc.serialGC.4g.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseParallelGC -Xms4g -Xmx4g -Xloggc:gc.parallelGC.4g.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g -Xloggc:gc.concMarkSweepGC.4g.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *      >java -XX:+UseG1GC -Xms4g -Xmx4g -Xloggc:gc.g1GC.4g.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps com.zbo.GCLogAnalysis
         *
         * 总结：
         *      gc 和 堆内存的关系
         *      1、增大内存可以降低gc次数和频率，但也会增加gc的耗时
         *      2、增大内存可以避免‘提升速率过快’的fullGC
         *
         *      gc之间的差异
         *      1、回收速度 g1 > cms > parallel > serial
         */
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}