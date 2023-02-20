package com.czl.rabbitmq.utils;

/**
 * @author caizhenlong
 * @create 2023/2/13
 */
public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
