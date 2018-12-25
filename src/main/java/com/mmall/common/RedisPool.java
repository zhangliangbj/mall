package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;//jedis连接池
    private static Integer maxTotal =  Integer.valueOf(PropertiesUtil.getProperty("redis.max.total","20"));   //最大连接数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中最大的idle状态（空闲的）的jedis实例的个数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle","2"));//在jedispool中最小的idle状态（空闲的）的jedis实例的个数
    private static Boolean testOnBorrow =  Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow","true"));//在borrow一个Jedis实例的时候，是否要进行验证操作，如果赋值true，那么得到的jedis实例肯定是可用的
    private static Boolean testOnReturn =  Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return","true"));//在borrow一个Jedis实例的时候，是否要进行验证操作，如果赋值true，则放回jedispool的jedis实例肯定是可用的

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");//在jedispool中最大的idle状态（空闲的）的jedis实例的个数
    private static Integer redisPort = Integer.valueOf(PropertiesUtil.getProperty("redis.port"));//在jedispool中最小的idle状态（空闲的）的jedis实例的个数

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候是否阻塞，true：阻塞，false:抛出异常

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis){
            pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
            pool.returnResource(jedis);
    }

    public static void main (String[] args){
        //Jedis jedis = pool.getResource();
        Jedis jedis = RedisPool.getJedis();
        jedis.set("geelykey","geelyvalue");
        RedisPool.returnResource(jedis);
        pool.destroy();//临时调用
        System.out.println("program is end");
    }

}
