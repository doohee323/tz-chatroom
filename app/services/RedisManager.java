package services;
import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
 
public class RedisManager {
	private static final String redisurl = Play.application().configuration().getString("redis.url");
    private static final RedisManager instance = new RedisManager();
    private static JedisPool pool;
    private RedisManager() {}
    public final static RedisManager getInstance() {
        return instance;
    }
    
   static  {
       // Create and set a JedisPoolConfig
       JedisPoolConfig poolConfig = new JedisPoolConfig();
       // Maximum active connections to Redis instance
       poolConfig.setMaxActive(1000);
       // Number of connections to Redis that just sit there
       // and do nothing
       poolConfig.setMaxIdle(25);
       // Minimum number of idle connections to Redis
       // These can be seen as always open and ready to serve
       poolConfig.setMinIdle(1);
       // Tests whether connection is dead when connection
       // retrieval method is called
       poolConfig.setTestOnBorrow(true);
       /* Some extra configuration */
       // Tests whether connection is dead when returning a
       // connection to the pool
       poolConfig.setTestOnReturn(true);
       // Tests whether connections are dead during idle periods
       poolConfig.setTestWhileIdle(true);
       // Maximum number of connections to test in each idle check
       poolConfig.setNumTestsPerEvictionRun(10);
       // Idle connection checking period
       poolConfig.setTimeBetweenEvictionRunsMillis(60000);
       // Maximum time, in milliseconds, to wait for a resource when exausted action is set to WHEN_EXAUSTED_BLOCK -->
       poolConfig.setMaxWait(30000);

       // Create the jedisPool
       pool = new JedisPool(poolConfig, redisurl, 6379);
    }
    public void release() {
        pool.destroy();
    }
    public Jedis getJedis() {
        return pool.getResource();
    }
    public void returnJedis(Jedis jedis) {
        pool.returnResource(jedis);
    }
}