package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *
 */
public class ConnectionBuilder {

    private ConnectionFactory factory = new ConnectionFactory();

    public ConnectionBuilder() {
        factory.setUsername(Constants.USERNAME);
        factory.setPassword(Constants.PASSWORD);
        factory.setHost(Constants.HOST);
        factory.setPort(Constants.PORT);
    }

    public Connection getConnection() {
        try {
            Connection connection = factory.newConnection();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
