package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public class BaseConsumerTest {

    private Connection connection;

    protected Channel channel;

    @Before
    public void before() throws IOException {
        connection = new ConnectionBuilder().getConnection();
        channel = connection.createChannel();
    }

    @After
    public void after(){
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
