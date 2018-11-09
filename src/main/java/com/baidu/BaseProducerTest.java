package com.baidu;

import com.baidu.model.constants.ExchangeConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.After;
import org.junit.Before;

/**
 * 发送者测试base类
 */
public class BaseProducerTest {

    //连接
    private Connection connection;

    //信道
    protected Channel channel;

    protected String exchange = "exchange";

    protected String queue = "queue";

    protected String route = "routingKey";

    @Before
    public void before() throws Exception {
        connection = new ConnectionBuilder().getConnection();
        channel = connection.createChannel();
        //声明direct类型的交换器
        channel.exchangeDeclare("exchange", ExchangeConstant.DIRECT, false, true, null);
        //声明队列
        channel.queueDeclare("queue", false, false, false, null);
        //队列和交换器使用路由键绑定,direct类型路由键必须完全匹配才能发送到队列
        channel.queueBind("queue", "exchange", "routingKey");
    }

    @After
    public void after() throws Exception{
        Thread.sleep(1000L * 10);
        //关闭连接
        if (connection != null){
            connection.close();
        }
    }

    protected void printSuccess(){
        System.out.println("操作成功");
    }
}
