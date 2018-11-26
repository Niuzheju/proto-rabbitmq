package com.baidu.multi_channel;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 为了使队列均匀地在每个节点上创建还可以使用以下方式
 */
public class MultiChannel {
    //多个Channel,创建队列时随机选用
    private static List<Channel> channelList = new ArrayList<>();

    public static void main(String[] args) {
        initChannelList();
    }

    private static void initChannelList() {
        String username = "root";
        String password = "root";
        String[] hosts = new String[]{"192.168.70.80:5672", "192.168.70.128:5672"};
        for (String host : hosts) {
            String[] strs = host.split(":");
            Connection conn = ConnectionBuilder.getNewConnection(strs[0], strs[1], username, password);
            if (conn != null) {
                try {
                    Channel channel = conn.createChannel();
                    if (channel != null) {
                        channelList.add(channel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
