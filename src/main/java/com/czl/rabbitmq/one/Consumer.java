package com.czl.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author caizhenlong
 * @create 2023/2/13
 */
public class Consumer {
    //队列名称
    public static final String QUEUE_NAME = "hello";

    //接收消息
    public static void main(String[] args) {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ的队列
        factory.setHost("192.168.130.135");
        //用户名 密码
        factory.setUsername("admin");
        factory.setPassword("123");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //声明 接受消息的回调
            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(new String(message.getBody()));
            };

            //声明  取消消息的回调
            CancelCallback cancelCallback = (consumerTag) -> {
                System.out.println("消费被中断！");
            };

            /*
             * 消费者消费消息，接受信息
             * 1、消费哪个队列
             * 2、消费成功之后，是否自动应答 true代表自动应答 false手动应答
             * 3、消费者未成功消费的回调
             * 4、消费者取消消费的回调*/
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
