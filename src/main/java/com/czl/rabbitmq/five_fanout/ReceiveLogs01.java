package com.czl.rabbitmq.five_fanout;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消息接收者1
 *
 * @author caizhenlong
 * @create 2023/2/20
 */
@SuppressWarnings({"all"})
public class ReceiveLogs01 {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //声明队列  临时队列
        /**
         * 临时队列。队列名称随机
         * 当消费者断开与队列的连接的时候，队列就自动删除
         */
        String queueName = channel.queueDeclare().getQueue();

        /**
         * 绑定交换机
         */
        channel.queueBind(queueName, EXCHANGE_NAME, "logs");
        System.out.println("等待接受消息，把接受到的消息显示在屏幕上！");

        //接受消息      Lambda表达式
        DeliverCallback deliverCallback = (consumerTag, message) ->
                System.out.println("接收到的消息：" + new String(message.getBody(), "UTF-8"));

        //消费者取消接收消息后回调方法
        CancelCallback cancelCallback = consumerTag -> {
        };

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
