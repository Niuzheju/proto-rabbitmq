package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

/**
 * 消费者base类
 */
public class BaseConsumerTest {

    private Connection connection;

    //信道,提供给子类操作rabbitmq
    protected Channel channel;

    @Before
    public void before() throws IOException {
        connection = new ConnectionBuilder().getConnection();
        channel = connection.createChannel();
    }

    //测试结束关闭连接
    @After
    public void after(){
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
