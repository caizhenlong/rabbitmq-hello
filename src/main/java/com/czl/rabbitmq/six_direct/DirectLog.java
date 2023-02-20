package com.czl.rabbitmq.six_direct;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * EmitLog一个发送消息，两个消费者接收
 *
 * @author caizhenlong
 * @create 2023/2/20
 */
public class DirectLog {
    //交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.print("请输入要发布的消息：");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes("UTF-8"));
            System.out.println("生成者发出消息：" + message);
        }

    }
}
