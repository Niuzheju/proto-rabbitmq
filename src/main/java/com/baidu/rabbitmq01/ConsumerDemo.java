package com.baidu.rabbitmq01;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConsumerDemo {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = new ConnectionBuilder().getConnection();
        Channel channel = connection.createChannel();
        try {
            channel.basicQos(64);
        } catch (ShutdownSignalException e) {
            e.printStackTrace();
        }
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume("queue_local", consumer);
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }
}
