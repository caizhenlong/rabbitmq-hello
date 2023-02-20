package com.czl.rabbitmq.four;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * 发布确认模式
 * 1.单个确认  使用的时间 比较哪种方式最好
 * 2.批量确认
 * 3.异步批量确认
 *
 * @author caizhenlong
 * @create 2023/2/14
 */
public class ConfirmMessage {

    //批量发消息个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 1.单个确认
        //ConfirmMessage.publishMessageIndividually();    //发布1000个单独确认消息，耗时2159ms

        //2.批量确认
        //ConfirmMessage.publishMessageBatch();   //发布1000个批量确认消息，耗时800ms

        //3.异步批量确认
        ConfirmMessage.publishMessageAsync();     //发布1000个异步发布确认消息，耗时47ms
                                                    //发布1000个异步发布确认消息，耗时101ms
    }

    // 1.单个确认
    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列生命
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认模式
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            //单个消息就马上进行发布确认
            boolean result = channel.waitForConfirms();
            if (result) {
                System.out.println("消息发送成功！");
            }
        }

        //总用时
        long countTime = System.currentTimeMillis() - begin;
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时" + countTime + "ms");
    }

    //2.批量发送消息
    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列生命
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认模式
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize = 100;


        //批量发送消息 批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

            //判断达到100条消息的时候 批量确认一次
            if (i % batchSize == 0) {
                //发布确认
                channel.waitForConfirms();
            }

        }

        //总用时
        long countTime = System.currentTimeMillis() - begin;
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息，耗时" + countTime + "ms");
    }


    //3.异步发布确认
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列生命
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认模式
        channel.confirmSelect();

        /**
         * 线程安全有序的hash表，适合于高并发的情况下
         * 1.轻松的将序号和消息关联
         * 2.轻松批量删除条目 只要给到序号
         * 3.支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        //消息确认成功 回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //如果是批量
            if (multiple) {
                //2-1.删除已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(deliveryTag);
            }

            System.out.println("确认的消息：" + deliveryTag);
        };
        //消息确认失败 回调函数
        /**
         * deliveryTag:消息的标记
         * multiple：是否批量确认
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //3-1.打印一下未确认的消息有哪些
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息是：" + message + ":::未确认的消息tag：" + deliveryTag);
        };
        //消息监听器 监听哪些消息成功了，哪些失败了
        channel.addConfirmListener(ackCallback, nackCallback);  //异步通知

        //开始时间
        long begin = System.currentTimeMillis();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            //1-1.记录下所有要发送的消息 消息总额
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }


        //总用时
        long countTime = System.currentTimeMillis() - begin;
        System.out.println("发布" + MESSAGE_COUNT + "个异步发布确认消息，耗时" + countTime + "ms");
    }
}
