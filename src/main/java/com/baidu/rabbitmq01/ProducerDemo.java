package com.baidu.rabbitmq01;

import com.baidu.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerDemo {
    private static final String EXCHANGE_NAME = "exchange_nzj";

    private static final String ROUTING_KEY = "routing_nzj";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        factory.setPort(Constants.PORT);
        factory.setUsername(Constants.USERNAME);
        factory.setPassword(Constants.PASSWORD);
        //设置虚拟主机
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
        channel.queueDeclare(Constants.QUEUE_NAME, true, false, false, null);
        channel.queueBind(Constants.QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        String message = "hello world";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        channel.close();
        connection.close();
    }
}
