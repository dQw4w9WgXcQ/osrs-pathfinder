package dev.dqw4w9wgxcq.pathfinder.pathfinder.redis;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record CacheResult(Status status, CacheState state) {
    public enum Status {
        HIT,
        MISS,
        MISS_COMPUTED_ELSEWHERE, // miss but did not acquire lock. waited for it to be computed by lock holder
    }
}
