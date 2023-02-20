package com.czl.rabbitmq.three;

import com.czl.rabbitmq.utils.RabbitMqUtils;
import com.czl.rabbitmq.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author caizhenlong
 * @create 2023/2/13
 * 消息在手动应答时是不丢失的，放回队列重新消费
 */
public class Worker04 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ack_queue";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2等待接受消息处理时间较长......");
        //采用手动应答
        boolean autoAck = false;

        //消息接受到，消费后回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            //沉睡1s
            SleepUtils.sleep(30);
            System.out.println("接收到的消息：" + new String(message.getBody(), "UTF-8"));
            //采用手动应答
            /*
             * 1.消息的标记 tag
             * 2.是否批量应答 falsbu不批量应答信道中的消  true：批量*/
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        //设置不公平分发  默认0-公平分发  1-代表不公平发
        //int prefetchCount = 1;

        //设置预取值  值为5
        int prefetchCount = 5;

        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑。");
        }));
    }
}
