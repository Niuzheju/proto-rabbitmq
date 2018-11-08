package com.baidu.rpc;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author niuzheju
 * @date 2018/11/7 21:32
 */
public class RpcServer {

    public static void main(String[] args) throws Exception {
        run();
    }

    private static void run() throws Exception {
        String requestQueue = "requestQueue";
        Connection connection = new ConnectionBuilder().getConnection();
        final Channel channel = connection.createChannel();
        //声明requestQueue
        channel.queueDeclare(requestQueue, false, false, true, null);
        //监听requestQueue
        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    //获取请求消息
                    System.out.println("receive request data is " + new String(body));
                } finally {
                    //把响应消息发送到回调队列properties.getReplyTo(),并使用请求的属性参数
                    channel.basicPublish("", properties.getReplyTo(), properties, "response data".getBytes());
                    //确认消息
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(requestQueue, false, consumer);

        System.out.println("server is start ...");
        new CountDownLatch(1).await();
    }
}
