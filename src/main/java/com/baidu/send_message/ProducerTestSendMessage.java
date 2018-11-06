package com.baidu.send_message;

import com.baidu.BaseProducerTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
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
}
