package com.baidu.send_message;

import com.baidu.BaseProducerTest;
import com.baidu.model.constants.ExchangeConstant;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

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
        channel.basicPublish("exchange", "routingKey", MessageProperties.PERSISTENT_TEXT_PLAIN, "message".getBytes());
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
        channel.basicPublish("exchange", "404", true, false, MessageProperties.TEXT_PLAIN, "test mandatory".getBytes());
    }

    /**
     * 备份交换器
     * 使用mandatory参数时需要在客户端写代码接收
     * 使用备份交换器可以把未被路由到队列的消息通过备份交换器存储在指定队列中
     * 在可能出现消息路由不到队列的交换器上设置参数alternate-exchange:备份交换器名
     */
    @Test
    public void test08() throws IOException {
        String normalExchange = "normalExchange";
        String normalQueue = "normalQueue";
        String aeExchange = "aeExchange";
        String unRouteQueue = "unRouteQueue";
        Map<String, Object> arg = new HashMap<>();
        arg.put("alternate-exchange", aeExchange);
        channel.exchangeDeclare(normalExchange, ExchangeConstant.DIRECT, false, true, arg);
        channel.queueDeclare(normalQueue, false, false, true, null);
        channel.exchangeDeclare(aeExchange, ExchangeConstant.FANOUT, true, false, null);
        channel.queueDeclare(unRouteQueue, true, false, false, null);
        channel.queueBind(normalQueue, normalExchange, "key1");
        channel.queueBind(unRouteQueue, aeExchange, "");
        channel.basicPublish(normalExchange, "key1", null, "test alternate-exchange".getBytes());
        printSuccess();
    }

    /**
     * 设置消息过期时间1
     * 单位:毫秒
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
     * 设置消息过期时间2
     * 通过队列设置消息过期时间, 整个队列的消息的过期时间是一样的
     * 单位毫秒
     */
    @Test
    public void test09() throws IOException {
        String test_ttl_queue = "test_ttl_queue";
        String test_ttl_exchange = "test_ttl_exchange";
        Map<String, Object> map = new HashMap<>();
        map.put("x-message-ttl", 6000 * 10);
        channel.queueDeclare(test_ttl_queue, false, false, true, map);
        channel.exchangeDeclare(test_ttl_exchange, ExchangeConstant.FANOUT, false, true, null);
        channel.queueBind(test_ttl_queue, test_ttl_exchange, "");
        channel.basicPublish(test_ttl_exchange, "test", null, "test_ttl".getBytes());
        printSuccess();
    }

    /**
     * 设置队列的过期时间
     * 单位:毫秒
     */
    @Test
    public void test10() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("x-expires", 6000 * 10);
        channel.queueDeclare("test_expires_queue", false, false, false, map);
        printSuccess();
    }

    /**
     * 死信队列
     */
    @Test
    public void test11() throws IOException {
        String exchangeDlx = "exchange.dlx";
        String exchangeNormal = "exchange.normal";
        String queueNormal = "queue.normal";
        String queueDlx = "queue.dlx";
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", exchangeDlx);
        map.put("x-message-ttl", 6000 * 10);
        map.put("x-dead-letter-routing-key", "key");
        channel.exchangeDeclare(exchangeNormal, ExchangeConstant.FANOUT, false, true, null);
        channel.exchangeDeclare(exchangeDlx, ExchangeConstant.DIRECT, false, true, null);
        channel.queueDeclare(queueNormal, false, false, true, map);
        channel.queueDeclare(queueDlx, false, false, true, null);
        channel.queueBind(queueNormal, exchangeNormal, "");
        channel.queueBind(queueDlx, exchangeDlx, "key");
        channel.basicPublish(exchangeNormal, "", null, "test-dlx-queue".getBytes());
        printSuccess();
    }

    /**
     * 使用消息ttl搭配死信队列实现延迟队列
     */
    @Test
    public void test12() throws IOException {
        Map<String, Long> times = new HashMap<>();
        times.put("5s", 5000L);
        times.put("10s", 10000L);
        times.put("30s", 30000L);
        times.put("1min", 1000L * 60);
        String[] ttls = new String[]{"5s", "10s", "30s", "1min"};
        channel.exchangeDeclare("exchange_core_delay", ExchangeConstant.DIRECT, true);
        for (String ttl : ttls) {
            String queueDlx = "queue_delay_" + ttl;
            String queueNormal = "queue_" + ttl;
            String exchangeDlx = "dlx_" + ttl;
            Map<String, Object> map = new HashMap<>();
            map.put("x-dead-letter-exchange", exchangeDlx);
            map.put("x-message-ttl", times.get(ttl));
            channel.queueDeclare(queueDlx, true, false, false, null);
            channel.queueDeclare(queueNormal, true, false, false, map);
            channel.exchangeDeclare(exchangeDlx, ExchangeConstant.FANOUT, false);
            channel.queueBind(queueNormal, "exchange_core_delay", ttl);
            channel.queueBind(queueDlx, exchangeDlx, "");
        }
        printSuccess();
    }

    /**
     * 优先级队列
     */
    @Test
    public void test13() throws IOException {
        String exchangeTest = "test-exchange";
        String queue = "priority-queue";
        String priority = "priority";
        Map<String, Object> map = new HashMap<>();
        //在队列上设置优先级,
        map.put("x-max-priority", 10);
        channel.exchangeDeclare(exchangeTest, ExchangeConstant.DIRECT, false);
        channel.queueDeclare(queue, false, false, true, map);
        channel.queueBind(queue, exchangeTest, priority);
        channel.basicPublish(exchangeTest, priority
                , new AMQP.BasicProperties.Builder().priority(10).build(), "priority-4".getBytes());
        printSuccess();
    }

    /**
     * 生产者确认-事务机制
     */
    @Test
    public void test14() throws IOException {
        //将信道设为事务模式
        channel.txSelect();
        try {
            channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, "message".getBytes());
            int i = 10 / 0;
            //提交
            channel.txCommit();
        } catch (Exception e) {
            //回滚
            channel.txRollback();
            e.printStackTrace();
        }
        printSuccess();
    }

    /**
     * 生产者确认-单个confirm方式
     */
    @Test
    public void test15() throws Exception {
        //开启生产者确认方式, 和事务机制互斥
        channel.confirmSelect();
        channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, "message".getBytes());
        //等待服务器确认, 此处会阻塞, 返回true证明保存成功
        if (!channel.waitForConfirms()) {
            //失败, 重新发送
            channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, "message".getBytes());
        }
        printSuccess();
    }


    /**
     * 生产者确认-批量confirm方式
     */
    @Test
    public void test16() throws Exception {
        channel.confirmSelect();
        int batchCount = 10;
        int count = 0;
        List<String> list = new ArrayList<>(20);
        while (true) {
            String msg = "message" + (++count);
            //把消息存起来
            list.add(msg);
            channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, (msg).getBytes());
            //每10次确认
            if (count == batchCount) {
                System.out.println("发起确认");
                if (!channel.waitForConfirms(1000L)) {
                    System.out.println("重新发送消息");
                    //失败, 重发所有消息
                    for (String s : list) {
                        channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, s.getBytes());
                    }
                    list.clear();
                } else {
                    //成功, 清空list
                    list.clear();
                }
                count = 0;
            }
        }
    }

    /**
     * 生产者确认-异步confirm回调方式
     */
    @Test
    public void test17() throws IOException {
        int i = 100;
        SortedSet<Long> confirmSet = new TreeSet<>();
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {

            //服务器确认消息时回调
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    System.out.println(deliveryTag);
                    //如果是最后一个,则全部清空
                    if (confirmSet.last().equals(deliveryTag)) {
                        confirmSet.clear();
                    } else {
                        //删除本条消息之前的数据
                        confirmSet.headSet(deliveryTag).clear();
                    }
                    System.out.println(confirmSet);
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }

            //服务器拒绝消息时回调
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
                //重新发送
            }
        });

        while (i-- > 0) {
            //生产者确认模式下, 获取下一个消息的deliveryTag
            long deliveryTag = channel.getNextPublishSeqNo();
            channel.basicPublish(exchange, route, MessageProperties.TEXT_PLAIN, "test confirm".getBytes());
            //把deliveryTag添加到集合中
            confirmSet.add(deliveryTag);
        }

    }


}
