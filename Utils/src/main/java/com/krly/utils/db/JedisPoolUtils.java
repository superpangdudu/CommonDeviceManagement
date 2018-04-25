package com.krly.utils.db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class JedisPoolUtils {

    public static JedisPool newJedisPoolInstance(String host, int port) {
        return newJedisPoolInstance(host, port, 5, 20, 3000);
    }

    public static JedisPool newJedisPoolInstance(String host, int port,
                                                 int maxIdle, int maxTotal, int maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);

        JedisPool jedisPool = new JedisPool(config, host, port);
        return jedisPool;
    }

    //===================================================================================
    public static void main(String[] args) {
        JedisPool jedisPool = newJedisPoolInstance("127.0.0.1", 6379);
        long startTime = System.currentTimeMillis();

        Jedis jedis = jedisPool.getResource();
        for (int n = 0; n < 500000; n++) {
            jedis.hset("tmp", "key", "value" + n);
            //String value = jedis.hget("tmp", "key");
            //System.out.println("item - " + n + ", jedis = " + jedis);
            //jedis.close();

        }

        System.out.println("took time: " + (System.currentTimeMillis() - startTime));
    }
}
