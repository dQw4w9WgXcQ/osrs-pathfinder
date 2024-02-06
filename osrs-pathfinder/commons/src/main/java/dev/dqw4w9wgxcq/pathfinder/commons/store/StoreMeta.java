package dev.dqw4w9wgxcq.pathfinder.commons.store;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record StoreMeta(String version, String createdAt, @Nullable String cacheMeta) {
    public StoreMeta(String version, Instant createdAt, @Nullable String cacheMeta) {
        this(version, createdAt.toString(), cacheMeta);
    }
}
