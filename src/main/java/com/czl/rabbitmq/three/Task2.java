package com.czl.rabbitmq.three;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author caizhenlong
 * @create 2023/2/13
 * 消息在手动应答时是不丢失、放回队列中重新消费
 */
public class Task2 {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ack_queue";

    //发送消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //队列名称、持久化、是否共享、是否自动删除、其他参数
        //设置队列持久化
        boolean durable = true;
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
        //控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            //设置消息持久化  MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息：" + message);
        }
    }
}
