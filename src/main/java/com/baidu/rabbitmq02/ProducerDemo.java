package com.baidu.rabbitmq02;

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
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        //创建默认队列
        String queue = channel.queueDeclare().getQueue();
        System.out.println(queue);
        channel.close();
        connection.close();
    }
}
