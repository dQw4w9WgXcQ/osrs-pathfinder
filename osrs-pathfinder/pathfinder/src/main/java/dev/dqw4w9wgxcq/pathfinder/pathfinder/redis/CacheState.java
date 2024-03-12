package dev.dqw4w9wgxcq.pathfinder.pathfinder.redis;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public record CacheState(Status status, @Nullable String json, @Nullable String errorMessage) {
    public enum Status {
        SUCCESS,
        FAILURE,
        PERMANENT_FAILURE,
    }

    public CacheState {
        if (status == Status.SUCCESS && json == null)
            throw new IllegalArgumentException("status is SUCCESS but json is null");

        if (status != Status.SUCCESS && json != null)
            throw new IllegalArgumentException("status is " + status + " but json is not null");

        if (status != Status.SUCCESS && errorMessage == null)
            throw new IllegalArgumentException("status is " + status + " but errorMessage is null");
    }

    public String jsonOrThrow() {
        if (json == null) {
            throw new IllegalStateException("json is null");
        }
        return json;
    }

    public String errorMessageOrThrow() {
        if (errorMessage == null) {
            throw new IllegalStateException("errorMessage is null");
        }
        return errorMessage;
    }
}
