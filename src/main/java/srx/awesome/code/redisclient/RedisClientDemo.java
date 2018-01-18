package srx.awesome.code.redisclient;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

public class RedisClientDemo {
    public static void main(String[] args) {
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //set the data in redis string
        jedis.set("tutorial-name", "Redis tutorial");
        // Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));
    }

    @Test
    public void demo1(){
        //1. Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //2. set the data in redis string
        jedis.set("username", "Roxin");
        //3. Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("username"));
        //4. Close the Redis connection;
        jedis.close();
    }

    @Test
    public void demo2(){
        //连接池设定
        JedisPoolConfig config = new JedisPoolConfig();
        //设定最大连接数
        config.setMaxTotal(30);
        //设置最大空闲连接数
        config.setMaxIdle(10);
        //创建连接池
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1");
        //获得服务资源
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        jedis.set("username", "Roxin By Jedis Pool");
        System.out.println(jedis.get("username"));
        jedis.close();
        jedisPool.close();
    }

    @Test
    public void demo3(){
        //连接池设定
        JedisPoolConfig config = new JedisPoolConfig();
        //设定最大连接数
        config.setMaxTotal(30);
        //设置最大空闲连接数
        config.setMaxIdle(10);
        //创建连接池
        JedisPool jedisPool = new JedisPool(config, "127.0.0.1");
        //获得服务资源
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        Transaction transaction = jedis.multi();
        transaction.set("username", "Roxin By Jedis Pool in transaction");
        System.out.println(transaction.get("username"));
        transaction.exec();
        System.out.println(jedis.get("username"));
        jedis.close();
        jedisPool.close();
    }
}
