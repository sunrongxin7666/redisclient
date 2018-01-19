package srx.awesome.code.redisclient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RedisClientDemo {
    private JedisPool jedisPool;


    public RedisClientDemo(){
        //连接池设定
        JedisPoolConfig config = new JedisPoolConfig();
        //设定最大连接数
        config.setMaxTotal(30);
        //设置最大空闲连接数
        config.setMaxIdle(10);
        //创建连接池
        jedisPool = new JedisPool(config, "127.0.0.1");
    }

    @After
    public void close(){
        if(jedisPool!=null)
            jedisPool.close();
    }

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
    public void ConnectionTest(){
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
    public void ConnectionPoolTest(){
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
    public void HashTest(){
        Jedis jedis = jedisPool.getResource();

        String hashKey = "hashKey";
        jedis.hset(hashKey,"user","Roxin");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("field"+i,"value"+i);
        }

        jedis.hmset(hashKey,map);

        Long hlen = jedis.hlen(hashKey);
        System.out.println("Hash Size in redis:: "+hlen);
        assert hlen==11;

        List<String> user = jedis.hmget(hashKey, "user");
        System.out.println("Stored string in redis:: "+ user);
        assert user.get(0).equals("Roxin");

        jedis.del(hashKey);
        jedis.close();
    }

    @Test
    public void ListTest(){
        Jedis jedis = jedisPool.getResource();
        String listKey = "LISTKEY";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listKey,"L-value");
        }

        List<String> list = jedis.lrange(listKey, 0, -1);//从第一个到最后一个,负数代表倒数第几个
        assert list.size() == 10;

        assert "L-value".equals(jedis.rpop(listKey));
        assert 9==jedis.llen(listKey);

        jedis.rpush(listKey,"R-valure");
        jedis.lrem(listKey,2,"L-value");//删除从左数2两个"L-value"元素
        jedis.lrem(listKey,0,"L-value"); //0表示删除全部"L-value"元素
        assert "R-valure".equals(jedis.lpop(listKey));

        jedis.del(listKey);
        jedis.close();

    }

    @Test
    public void SetTest(){
        Jedis jedis = jedisPool.getResource();
        String setKey1 = "SETKEY-1";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(setKey1,"value-"+i);
        }

        assert 10 == jedis.scard(setKey1); //获得元素个数
        jedis.sadd(setKey1,"value-1");
        assert 10 == jedis.scard(setKey1);

        String s= jedis.srandmember(setKey1);//随机获取一个元素
        assert jedis.sismember(setKey1,s);//是否为集合成员

        String setKey2 = "SETKEY-2";
        for (int i = 1; i < 11; i++) {
            jedis.sadd(setKey2,"value-"+i);
        }

        assert jedis.sdiff(setKey1,setKey2).size() == 1;//补集
        assert jedis.sinter(setKey1,setKey2).size() == 9;//交集
        assert jedis.sunion(setKey1,setKey2).size() == 11;//并集

        jedis.del(setKey1,setKey2);
        jedis.close();
    }

    @Test
    public void SortedSetTest(){
        Jedis jedis = jedisPool.getResource();
        String sortedSetKey = "SORTEDSETKEY";

        for (int i = 0; i < 10; i++) {
            jedis.zadd(sortedSetKey,i*10,"v-"+i);
        }
        assert 10 == jedis.zcard(sortedSetKey);//获得集合中元素个数

        assert 20 == (jedis.zscore(sortedSetKey,"v-2"));//获得集合中元素对应的分数

        Set<String> set = jedis.zrange(sortedSetKey, 0, -2);//从第一个到倒数第二个
        assert 9 == set.size() ;
        assert !set.contains("v-9");

        jedis.zincrby(sortedSetKey,20,"v-1");//让元素的分数增长20

        assert 30 == jedis.zscore(sortedSetKey,"v-1");
        assert 3 == jedis.zcount(sortedSetKey,20,30);//或者分数段中元素个数

        jedis.del(sortedSetKey);
        jedis.close();
    }

    @Test
    public void KeyTest(){
        Jedis jedis = jedisPool.getResource();
        String key = "TESTKEY-1";
        String key2 = "TESTKEY-2";

        jedis.set(key2,"");//设置键值
        jedis.rename(key2,key);//键值重命名

        System.out.println("Key Type:"+jedis.type(key));//键值的类型

        assert jedis.exists(key);//键值是否存在
        jedis.expire(key,1);//设置键值过期时间
        assert 1 == jedis.ttl(key);//查看键值过期时间

        try {
            Thread.sleep(2000);//睡眠2s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert !jedis.exists(key);//键值已过期，不存在
    }
    @Test
    public void TransactionTest(){
        //获得服务资源
        Jedis jedis = jedisPool.getResource();

        jedis.select(1);
        Transaction transaction = jedis.multi();//开启事务
        transaction.set("username", "Roxin in transaction1");
        System.out.println(transaction.get("username"));
        transaction.exec();//提交事务
        System.out.println(jedis.get("username"));

        transaction = jedis.multi();//开启事务
        transaction.set("username", "Roxin in transaction2");
        System.out.println(transaction.get("username"));
        transaction.discard();//撤销事务

        System.out.println(jedis.get("username"));
        jedis.close();
    }
}
