package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送者测试base类
 */
public class BaseProducerTest {

    //连接
    private Connection connection;

    //信道
    protected Channel channel;

    @Before
    public void before() throws Exception {
        connection = new ConnectionBuilder().getConnection();
        channel = connection.createChannel();
        //声明direct类型的交换器
        channel.exchangeDeclare("exchange", "direct", false, true, null);
        //声明队列
        channel.queueDeclare("queue", false, false, false, null);
        //队列和交换器使用路由键绑定,direct类型路由键必须完全匹配才能发送到队列
        channel.queueBind("queue", "exchange", "routingKey");
    }

    @After
    public void after() throws IOException{
        //关闭连接
        if (connection != null){
            connection.close();
        }
    }
}
