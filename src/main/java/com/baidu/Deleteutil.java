package com.baidu;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Deleteutil {
    public static void delete(Integer type, String... name) throws Exception {
        Connection connection = new ConnectionBuilder().getConnection();
        Channel channel = connection.createChannel();
        if (type == 0) {
            for (int i = 0; i < name.length; i++) {
                channel.exchangeDelete(name[i]);
            }
        } else {
            for (int i = 0; i < name.length; i++) {
                channel.queueDelete(name[i]);
            }
        }
    }
}
