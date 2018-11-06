package com.baidu.consumer_message;

import com.baidu.BaseConsumerTest;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

public class TestConsumerMessage extends BaseConsumerTest {

    /**
     * 消费:推模式
     * 持续性订阅,channel和connection关闭之前会一直消费队列上的消息
     */
    @Test
    public void test01() throws IOException {
        boolean autoAck = false;
        //最大接收消息个数
        channel.basicQos(64);
        /*
         * queue:队列名称
         * autoAck:是否自动确认
         * consumerTag:唯一的消费者标签
         * callback:回调函数
         */
        channel.basicConsume("queue", autoAck, "consumerTag", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                System.out.println("routingKey:" + envelope.getRoutingKey());
//                System.out.println("contentType:" + properties.getContentType());
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(new String(body));
                //手动确认消费
                channel.basicAck(envelope.getDeliveryTag(), false);
//                channel.basicCancel(consumerTag);
            }
        });

        System.in.read();
    }

    /**
     * 消费:拉模式
     * 消费最近一条消息,仅消费一条时使用此模式
     */
    @Test
    public void test02() throws IOException {
        GetResponse response = channel.basicGet("queue", false);
        System.out.println(new String(response.getBody()));
        channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
    }

    /**
     * 测试回调函数其他方法
     * handleDelivery方法,由多线程调用,如果想阻止持续订阅,需要在第一次消费之后取消订阅
     */
    @Test
    public void test03() throws IOException, InterruptedException {
        boolean autoAck = false;
        //消费者订阅了一个队列后,就不能再声明一个队列, 必须先取消订阅, 并将信道置为传输
        channel.basicConsume("queue", autoAck, "consumerTag", new DefaultConsumer(channel) {
            private int count;

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ++count;
                System.out.println(new String(body) + ", count=" + count + ",tag=" + consumerTag);
                //count=100时把之前的消息全部拒绝
                if (count == 100){
                    channel.basicNack(envelope.getDeliveryTag(), true, true);
                }
            }

            //最先被调用
            @Override
            public void handleConsumeOk(String consumerTag) {
                System.out.println("消费成功-->" + consumerTag);
                super.handleConsumeOk(consumerTag);
            }

            @Override
            public void handleCancelOk(String consumerTag) {
                System.out.println("取消订阅成功-->" + consumerTag);
                super.handleCancelOk(consumerTag);
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                System.out.println("连接已关闭-->" + consumerTag);
                super.handleShutdownSignal(consumerTag, sig);
            }
        });


        Thread.sleep(1000L * 20);
    }

}
