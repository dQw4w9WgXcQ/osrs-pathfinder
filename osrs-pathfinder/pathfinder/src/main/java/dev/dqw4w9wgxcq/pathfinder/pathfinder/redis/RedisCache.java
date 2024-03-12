package dev.dqw4w9wgxcq.pathfinder.pathfinder.redis;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@ApiStatus.Internal
public class RedisCache {
    private static final int COMPUTE_LOCK_EXPIRE = 10; // seconds
    private static final Gson gson = new Gson();

    private final JedisPooled redis;

    public RedisCache(String host, int port) {
        var poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(1));
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));
        this.redis = new JedisPooled(poolConfig, host, port);
    }

    public CacheResult computeIfAbsent(String key, Compute compute) {
        var hitJson = redis.get(key);
        if (hitJson != null) {
            return new CacheResult(CacheResult.Status.HIT, gson.fromJson(hitJson, CacheState.class));
        }

        if (acquireComputeLock(key)) {
            try {
                CacheState state;
                int expire = -1;
                try {
                    var data = compute.computeJson();
                    state = new CacheState(CacheState.Status.SUCCESS, data, null);
                } catch (Compute.ComputeException e) {
                    log.warn("error", e);
                    state = new CacheState(CacheState.Status.FAILURE, null, e.getMessage());
                    expire = e.expire();
                } catch (Compute.FatalComputeException e) {
                    log.error("fatal error", e);
                    state = new CacheState(CacheState.Status.PERMANENT_FAILURE, null, e.getMessage());
                }

                if (expire != -1) {
                    assert state.status() == CacheState.Status.FAILURE;

                    redis.setex(key, expire, gson.toJson(state));
                } else {
                    redis.set(key, gson.toJson(state));
                }

                return new CacheResult(CacheResult.Status.MISS, state);
            } finally {
                releaseComputeLock(key);
            }
        } else {
            // we are not the lock holder
            // poll until lock holder computes result
            var end = Instant.now().plusSeconds(COMPUTE_LOCK_EXPIRE);
            while (Instant.now().isBefore(end)) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("interrupted", e);
                }

                var json = redis.get(key);
                if (json != null) {
                    return new CacheResult(
                            CacheResult.Status.MISS_COMPUTED_ELSEWHERE, gson.fromJson(json, CacheState.class));
                }
            }

            throw new RuntimeException("timed out waiting for lock holder to compute result");
        }
    }

    private boolean acquireComputeLock(String key) {
        var s = redis.set(key + ":lock", "1", SetParams.setParams().nx().ex(COMPUTE_LOCK_EXPIRE));
        if ("OK".equals(s)) {
            return true;
        } else if (s == null) {
            return false;
        } else {
            throw new IllegalStateException("unexpected response from redis .set: " + s);
        }
    }

    private void releaseComputeLock(String key) {
        redis.del(key + ":lock");
    }
}
