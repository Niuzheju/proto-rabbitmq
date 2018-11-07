package com.baidu.rpc;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.*;

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
        channel.queueDeclare(requestQueue, false, false, true, null);
        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    System.out.println("receive request data is " + new String(body));
                } finally {
                    channel.basicPublish("", properties.getReplyTo(), properties, "response data".getBytes());
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(requestQueue, false, consumer);

        System.out.println("server is start ...");
        new CountDownLatch(1).await();
    }
}
