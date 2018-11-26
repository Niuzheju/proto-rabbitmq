package com.baidu.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class HttpUtil {

    public static String sendMsg(String url, Map<String, Object> map) throws Exception {
        //验证器
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        //设置用户名,密码
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("root", "root"));
        //创建客户端,使用验证器
        CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        URI uri = new URIBuilder(url).build();
        HttpPut put = new HttpPut();
        //设置参数
        put.setEntity(new StringEntity(JSON.toJSONString(map), ContentType.APPLICATION_JSON));
        //设置url
        put.setURI(uri);
        String msg = null;
        try {
            msg = client.execute(put, (response) -> response.getEntity().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String sendPost(String url, Map<String, Object> map) {
        String msg = null;
        try {
            //验证器
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            //设置用户名,密码
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("root", "root"));
            //创建客户端,使用验证器
            CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            URI uri = new URIBuilder(url).build();
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(JSON.toJSONString(map), ContentType.APPLICATION_JSON));
            post.setURI(uri);
            msg = client.execute(post, (response) -> response.getEntity().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}
