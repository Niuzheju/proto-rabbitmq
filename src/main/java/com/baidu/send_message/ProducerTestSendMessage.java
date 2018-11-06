package com.baidu.send_message;

import com.baidu.BaseProducerTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProducerTestSendMessage extends BaseProducerTest {

    /**
     * 发送信息基本模式
     */
    @Test
    public void test01() throws Exception {
        for (int i = 0; i < 100; i++) {
            channel.basicPublish("exchange", "routingKey", null, (i + "message").getBytes());
        }
    }

    /**
     * 增加一些特定属性
     */
    @Test
    public void test02() throws IOException {
        channel.basicPublish("exchange", "routingKey",  MessageProperties.PERSISTENT_TEXT_PLAIN, "message".getBytes());
    }

    /**
     * 自定义属性
     */
    @Test
    public void test03() throws IOException {
        channel.basicPublish("exchange", "routingKey"
                , new AMQP.BasicProperties().builder().contentType("text/plain").deliveryMode(2).priority(1).userId("hidden").build()
                , "message".getBytes());
    }

    /**
     * headers属性
     */
    @Test
    public void test04() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("location", "here");
        map.put("time", "today");
        channel.basicPublish("exchange", "routingKey"
                , new AMQP.BasicProperties().builder().headers(map).build()
                , "message".getBytes());
    }

    /**
     * 设置过期时间
     */
    @Test
    public void test05() throws IOException, NoSuchMethodException {
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("exchange", "routingKey"
                    , new AMQP.BasicProperties().builder().expiration("60000").build()
                    , this.getClass().getMethod("test05").toString().getBytes());
        }
    }

    /**
     * topic类型交换器
     */
    @Test
    public void test06() throws IOException {
        String exchange_topic = "exchange_topic";
        String queue001 = "queue001";
        channel.exchangeDeclare(exchange_topic, "topic", false, true, null);
        channel.queueDeclare(queue001, false, false, true, null);
        channel.queueBind(queue001, exchange_topic, "*.niuzj.#");
        channel.basicPublish(exchange_topic, "com.niuzj.test", null, "hello world".getBytes());
    }

    /**
     * mandatory参数和immediate参数
     * mandatory为true时
     * 没有投递到队列的消息会被退回
     * 使用addReturnListener方法添加监听器, 处理退回消息
     * immediate为true时
     * 如果消息投递到的队列没有消费者连接, 那么消息将不会投递到该队列,
     * 如果匹配的队列都没有消费者消息将会退回, 使用addReturnListener方法处理退回消息
     * 3.0版本后immediate已不支持, 使用会出现ForgivingExceptionHandler抛出的异常
     * 在服务器的$RABBITMQ_HOME/var/log/rabbitmq/rabbit@localhost.log中也会有错误日志
     */
    @Test
    public void test07() throws IOException {
        /*
            replyCode: 消息退回状态码
            replyText: 消息退回描述
            exchange: 交换器
            routingKey: 路由键
            properties: 设置的参数
            body: 退回消息
         */
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            System.out.println("return message " + new String(body));
        });
        channel.basicPublish("exchange", "routingKey", false,  true, MessageProperties.TEXT_PLAIN, "test mandatory".getBytes());

    }

}
