    package com.czl.rabbitmq.utils;

    import com.rabbitmq.client.Channel;
    import com.rabbitmq.client.Connection;
    import com.rabbitmq.client.ConnectionFactory;

    import java.io.IOException;
    import java.util.concurrent.TimeoutException;

    /**
     * @author caizhenlong
     * @create 2023/2/13
     * 连接工厂创建信道的工具类
     */
    public class RabbitMqUtils {
        //创建连接信道
        public static Channel getChannel() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.130.135");
            factory.setUsername("admin");
            factory.setPassword("123");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            return channel;
        }
    }
