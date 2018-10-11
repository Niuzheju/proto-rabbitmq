package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BaseProducerTest {

    private Connection connection;

    protected Channel channel;

    @Before
    public void before() throws Exception {
        connection = new ConnectionBuilder().getConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare("exchange", "direct", false, true, null);
        channel.queueDeclare("queue", false, false, false, null);
        channel.queueBind("queue", "exchange", "routingKey");
    }

    @After
    public void after() throws IOException, TimeoutException {
        if (connection != null){
            connection.close();
        }
    }
}
