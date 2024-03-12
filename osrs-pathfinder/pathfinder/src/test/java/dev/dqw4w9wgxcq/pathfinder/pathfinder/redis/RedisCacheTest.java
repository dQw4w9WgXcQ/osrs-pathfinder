package dev.dqw4w9wgxcq.pathfinder.pathfinder.redis;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

// todo test env
@Disabled
class RedisCacheTest {
    @Test
    void test() throws ExecutionException, InterruptedException {
        var exe = Executors.newCachedThreadPool();
        var cache = new RedisCache("127.0.0.1", 6379);
        var key = "RedisCacheTest#test()";

        var future1 = exe.submit(() -> cache.computeIfAbsent(key, () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            return "some data";
        }));

        Thread.sleep(100);
        var future2 = exe.submit(() -> cache.computeIfAbsent(key, () -> "some data 2"));

        var result1 = future1.get();
        var result2 = future2.get();

        assertEquals(CacheResult.Status.MISS, result1.status());
        assertEquals(CacheResult.Status.MISS_COMPUTED_ELSEWHERE, result2.status());

        var result3 = cache.computeIfAbsent(key, () -> "some data 3");
        assertEquals(CacheResult.Status.HIT, result3.status());
    }
}
