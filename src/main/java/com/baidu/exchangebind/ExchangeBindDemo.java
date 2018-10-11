package com.baidu.exchangebind;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 交换器绑定
 */
public class ExchangeBindDemo{
    public static void main(String[] args) throws Exception {
        Connection connection = new ConnectionBuilder().getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("dest", "fanout", false, true, null);
        channel.exchangeDeclare("source", "direct", false, true, null);
        channel.queueDeclare("queue_local", false, false, true, null);
        //交换器source绑定到交换器dest上,路由键为key1
        channel.exchangeBind("dest", "source", "key1");
        //交换器dest和队列queue_local绑定, dest为fanout,所以路由键为""
        channel.queueBind("queue_local", "dest", "");
        //发送消息到source上,指定的路由键是与交换器dest绑定的路由键
        channel.basicPublish("source", "key1", null, "exchange_bind".getBytes());
        channel.close();
        connection.close();
    }

}
