package com.czl.rabbitmq.six_direct;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author caizhenlong
 * @create 2023/2/20
 */
public class ReceiveLogsDirect02 {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.queueDeclare("disk", false, false, false, null);
        channel.queueBind("disk", EXCHANGE_NAME, "error");

        //接受消息      Lambda表达式
        DeliverCallback deliverCallback = (consumerTag, message) ->
                System.out.println("接收到的消息：" + new String(message.getBody(), "UTF-8"));

        //消费者取消接收消息后回调方法
        CancelCallback cancelCallback = consumerTag -> {
        };
        channel.basicConsume("disk", deliverCallback, cancelCallback);
    }
}
