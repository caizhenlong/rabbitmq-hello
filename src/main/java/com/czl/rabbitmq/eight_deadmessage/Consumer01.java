package com.czl.rabbitmq.eight_deadmessage;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 死信队列
 *
 * @author caizhenlong
 * @create 2023/2/20
 */
public class Consumer01 {

    //普通交换机
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    //死信交换机
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列名称
    public static final String NORMAL_QUEUE = "normal_queue";

    //死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";


    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //声明死信和普通交换机 类型为 direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //正常队列绑定死信队列信息
        Map<String, Object> params = new HashMap<>();
        //过期时间 ms   10s=10000ms
        //params.put("x-message-ttl", 10000);

        //正常队列设置死信交换机 参数 key 是固定值
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key,去绑定死信队列 参数 key 是固定值
        params.put("x-dead-letter-routing-key", "lisi");
        //设置正常队列的长度的限制
        //params.put("x-max-length", 6 );

        //正常队列
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, params);
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");

        //死信队列
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");


        DeliverCallback deliverCallback = (consumerTag, message) ->
        {
            String msg = new String(message.getBody(), "UTF-8");
            if (msg.equals("info5")) {
                System.out.println("Consumer01 接收到消息" + msg + "：此消息是被C1拒绝的");
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            } else {
                System.out.println("Consumer01 接收到消息" + msg+",不批量应答");
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
        };


        CancelCallback cancelCallback = (consumerTag) -> {
        };

        //开启手动应答
        //普通队列
        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, cancelCallback);
    }
}
