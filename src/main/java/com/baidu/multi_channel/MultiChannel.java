package com.baidu.multi_channel;

import com.baidu.ConnectionBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 为了使队列均匀地在每个节点上创建还可以使用以下方式
 */
public class MultiChannel {
    //多个Channel,创建队列时随机选用
    private static List<Channel> channelList = new ArrayList<>();

    private static Random random = new Random();

    public static void main(String[] args) throws IOException {
        initChannelList();
        createQueue();
    }

    private static void createQueue() throws IOException {
        Channel channel = channelList.get(random.nextInt(2));
        channel.queueDeclare("queue.error", true, false, false, null);
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
