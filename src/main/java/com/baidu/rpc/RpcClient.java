package com.baidu.rpc;


import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;

/**
 * @author niuzheju
 * @date 2018/11/7 21:47
 */
public class RpcClient {

    public static void main(String[] args) throws Exception {
        String requestQueue = "requestQueue";
        String correlationId = UUID.randomUUID().toString();
        Connection connection = new ConnectionBuilder().getConnection();
        Channel channel = connection.createChannel();
        String replyTo = "client_replyTo";
        channel.queueDeclare(replyTo, false, false, true, null);
        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                if (!correlationId.equals(properties.getCorrelationId())) {
                    channel.basicReject(deliveryTag, true);
                    return;
                }
                System.out.println(new String(body));
                channel.basicAck(deliveryTag, false);
                connection.close(200, "exit success");
                System.out.println("client exit ...");
                System.exit(0);
            }
        };

        channel.basicConsume(replyTo, consumer);

        channel.basicPublish("", requestQueue
                , new AMQP.BasicProperties().builder().replyTo(replyTo).correlationId(correlationId).build()
                , "client request".getBytes());

        Thread.sleep(Long.MAX_VALUE);

    }
}
