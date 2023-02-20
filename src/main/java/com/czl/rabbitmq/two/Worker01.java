package com.czl.rabbitmq.two;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author caizhenlong
 * @create 2023/2/13
 * 工作线程（相当于消费者）
 */
public class Worker01 {
    //队列名称
    public static final String QUEUE_NAME = "hello";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();

        //接收消息后的回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String receivedMessage = new String(message.getBody());
        };
        //取消消息后的回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };

        /*
         * 消费者消费消息，接受信息
         * 1、消费哪个队列
         * 2、消费成功之后，是否自动应答 true代表自动应答 false手动应答
         * 3、消费者未成功消费的回调
         * 4、消费者取消消费的回调
         */
        System.out.println("C3等待接受消息......");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
