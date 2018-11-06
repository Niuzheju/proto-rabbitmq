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

        //监听channel关闭
        channel.addShutdownListener((cause) -> {
            System.out.println(cause.getMessage());
            System.out.println(cause.getReason());
        });

        //监听连接关闭
        connection.addShutdownListener((cause) -> {
            System.out.println(connection.isOpen());
            System.out.println("连接关闭");
            System.out.println(cause.getMessage());
            System.out.println(cause.getReason());
        });
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
