package com.baidu.log;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 监听日志
 * 交换器amq.rabbitmq.log绑定日志队列
 * 分别为
 * queue            routingKey
 * queue.info       info
 * queue.debug      debug
 * queue.warning    warning
 * queue.error      error
 */
public class LogMonitor {
    public static void main(String[] args) throws Exception {
        Connection conn = new ConnectionBuilder().getConnection();
        Channel infoChannel = conn.createChannel();
        Channel debugChannel = conn.createChannel();
        Channel warningChannel = conn.createChannel();
        Channel errorChannel = conn.createChannel();
        final boolean ack = false;
        infoChannel.basicConsume("queue.info", ack, "INFO", new InternalConsumer(infoChannel));
        debugChannel.basicConsume("queue.debug", ack, "DEBUG", new InternalConsumer(debugChannel));
        warningChannel.basicConsume("queue.warning", ack, "WARNING", new InternalConsumer(warningChannel));
        errorChannel.basicConsume("queue.error", ack, "ERROR", new InternalConsumer(errorChannel));

        new CountDownLatch(1).await();


    }

    static class InternalConsumer extends DefaultConsumer {

        private InternalConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println(consumerTag + ":" + new String(body));
            getChannel().basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
