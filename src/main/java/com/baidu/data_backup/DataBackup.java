package com.baidu.data_backup;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ConnectionBuilder;
import com.baidu.utils.HttpUtil;
import com.rabbitmq.client.Channel;

import java.io.*;
import java.net.URLEncoder;

/**
 * 使用备份的json文件恢复队列,交换器,绑定
 */
public class DataBackup {

    private static String data;

    private static Channel channel;

    private static String base = "http://192.168.70.80:15672/api/";

    private static String queueUrl = base + "queues/";

    private static String exchangeUrl = base + "exchanges/";

    private static String bindingUrl = base + "bindings/";

    static {

        init();

        if (data == null || "".equals(data)) {
            throw new RuntimeException("data 初始化失败");
        }

        try {
            channel = new ConnectionBuilder().getConnection().createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (channel == null)
            throw new RuntimeException("rabbit 连接失败");
    }

    public static void main(String[] args) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray queues = jsonObject.getJSONArray("queues");
        JSONArray exchanges = jsonObject.getJSONArray("exchanges");
        JSONArray bindings = jsonObject.getJSONArray("bindings");

        createQueues(queues);
        createExchanges(exchanges);
        createBindings(bindings);
    }

    private static void createBindings(JSONArray bindings) {
        if (bindings == null || bindings.size() == 0) {
            return;
        }
        for (int i = 0; i < bindings.size(); i++) {
            JSONObject jsonObject = bindings.getJSONObject(i);
            String url = bindingUrl + encoder(jsonObject.getString("vhost")) + "/";
            if (jsonObject.getString("destination_type").equals("queue")) {
                url = url + "e/" + encoder(jsonObject.getString("source")) + "/q/" + encoder(jsonObject.getString("destination"));
            } else {
                url = url + "e/" + encoder(jsonObject.getString("source")) + "/e/" + encoder(jsonObject.getString("destination"));
            }
            String s = HttpUtil.sendPost(url, jsonObject);
            System.out.println(s);
        }
    }

    //创建交换器
    private static void createExchanges(JSONArray exchanges) throws Exception {
        if (exchanges == null || exchanges.size() == 0) {
            return;
        }
        for (int i = 0; i < exchanges.size(); i++) {
            JSONObject jsonObject = exchanges.getJSONObject(i);
            String s = HttpUtil.sendMsg(exchangeUrl + encoder(jsonObject.getString("vhost")) + "/" + encoder(jsonObject.getString("name")), jsonObject);
            System.out.println(s);
        }
    }

    //创建队列
    private static void createQueues(JSONArray queues) throws Exception {
        if (queues == null || queues.size() == 0) {
            return;
        }
        String[] nodes = new String[]{"rabbit@localhost", "rabbit@node2", "rabbit@node3"};
        for (int i = 0; i < queues.size(); i++) {
            JSONObject data = queues.getJSONObject(i);
            //node参数指定队列在哪个节点创建
            data.put("node", nodes[0]);
            String s = HttpUtil.sendMsg(queueUrl + encoder(data.getString("vhost")) + "/" + encoder(data.getString("name")), data);
            System.out.println(s);
        }
    }

    private static void init() {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = DataBackup.class.getClassLoader().getResourceAsStream("rabbit_meta.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        char[] chars = new char[1024];
        int len = -1;
        try {
            while ((len = reader.read(chars)) != -1) {
                sb.append(chars, 0, len);
                chars = new char[1024];
            }
            reader.close();
            data = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encoder(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
