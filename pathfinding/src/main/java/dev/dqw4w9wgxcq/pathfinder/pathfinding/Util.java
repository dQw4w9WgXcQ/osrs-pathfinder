package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class Util {
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    public static <T> T await(Future<T> future) {
        return future.get();
    }
}
