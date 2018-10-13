package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 *rabbitmq连接工厂类
 */
public class ConnectionBuilder {

    private ConnectionFactory factory = new ConnectionFactory();

    public ConnectionBuilder() {
        factory.setUsername(Constants.USERNAME);
        factory.setPassword(Constants.PASSWORD);
        factory.setHost(Constants.HOST);
        factory.setPort(Constants.PORT);
        //通过设置uri连接,vhost暂时设置失败
//        try {
//            factory.setUri("amqp://root:root@192.168.70.80:5672/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
