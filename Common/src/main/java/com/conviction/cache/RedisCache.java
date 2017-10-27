package com.conviction.cache;

import com.conviction.common.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisCache extends AbstractCache
{
    private static final Logger logger;

    private static final String REDIS_CLUSTER_PROPERTY_PREFIX;
    private static final String REDIS_MAX_IDLE;
    private static final String REDIS_MAX_WAIT;
    private static final String REDIS_TIMEOUT;
    private static final String REDIS_MAX_TOTAL;
    private static final String REDIS_MIN_IDLE;
    private static final String REDIS_TEST_ON_BORROW;

    static
    {
        logger = LoggerFactory.getLogger(RedisCache.class);

        REDIS_CLUSTER_PROPERTY_PREFIX = "redis.cluster";
        REDIS_MAX_IDLE = "redis.maxIdle";
        REDIS_MAX_WAIT = "redis.maxWait";
        REDIS_TIMEOUT = "redis.timeout";
        REDIS_MAX_TOTAL = "redis.maxTotal";
        REDIS_MIN_IDLE = "redis.minIdle";
        REDIS_TEST_ON_BORROW = "redis.testOnBorrow";
    }

    private JedisCluster jedisCluster;
    private JedisPoolConfig jedisPoolConfig;

    public RedisCache()
    {
        super("RedisCache");
        this.jedisCluster = null;
        this.jedisPoolConfig = new JedisPoolConfig();
        initRedisConfig();
    }

    private void initRedisConfig()
    {
        String maxIdle = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_MAX_IDLE);
        String maxWait = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_MAX_WAIT);
        String maxTotal = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_MAX_TOTAL);
        String minIdle = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_MIN_IDLE);
        String testOnBorrow = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_TEST_ON_BORROW);

        this.jedisPoolConfig.setMaxIdle(Integer.valueOf(maxIdle));
        this.jedisPoolConfig.setMaxWaitMillis(Long.valueOf(maxWait));
        this.jedisPoolConfig.setMaxTotal(Integer.valueOf(maxTotal));
        this.jedisPoolConfig.setMinIdle(Integer.valueOf(minIdle));
        this.jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
    }

    @Override
    protected void doStart()
    {
        String timeout = this.iEnvironment.getProperty(CommonConstant.MODEL_NAME, REDIS_TIMEOUT);
        Map<String, String> redisClusterProperties =
                this.iEnvironment.getProperties(CommonConstant.MODEL_NAME, REDIS_CLUSTER_PROPERTY_PREFIX);
        Set<HostAndPort> hostAndPortSet = redisClusterProperties.values().stream()
                .map(HostAndPort::parseString)
                .collect(Collectors.toSet());
        this.jedisCluster = new JedisCluster(hostAndPortSet, Integer.valueOf(timeout), jedisPoolConfig);
    }

    @Override
    protected void doStop()
    {
        try
        {
            this.jedisCluster.close();
        }
        catch (IOException e)
        {
            logger.error("close redis client fail!", e);
        }
    }
}
