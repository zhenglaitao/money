package com.daily.record.money.common;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisService {
	private static final Logger logger = Logger.getLogger(RedisService.class);

	public static JedisPool pool = null;

	private static final RedisService INSTANCE = new RedisService();

	public static RedisService getInstance() {
		return INSTANCE;
	}

	public synchronized void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(30);
		//config.setMinIdle(5);
		config.setMaxIdle(5);
		config.setMaxWaitMillis(5000);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config,"127.0.0.1", 6379);
		logger.info("redis init success");
	}

	public String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			if (pool == null) {
				init();
				logger.error("init pool error,init again...... ");
			}
			jedis = pool.getResource();
			value = jedis.get(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return value;
	}
	
	public void set(String key,String val) {
		Jedis jedis = null;
		try {
			if (pool == null) {
				init();
				logger.error("init pool error,init again...... ");
			}
			jedis = pool.getResource();
		    jedis.set(key, val);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
}
