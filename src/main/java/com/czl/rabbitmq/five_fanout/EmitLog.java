package com.czl.rabbitmq.five_fanout;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * EmitLog一个发送消息，两个消费者接收
 *
 * @author caizhenlong
 * @create 2023/2/20
 */
public class EmitLog {
    //交换机名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        System.out.print("请输入要发布的消息：");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "logs", null, message.getBytes("UTF-8"));
            System.out.println("生成者发出消息：" + message);
        }

    }
}
