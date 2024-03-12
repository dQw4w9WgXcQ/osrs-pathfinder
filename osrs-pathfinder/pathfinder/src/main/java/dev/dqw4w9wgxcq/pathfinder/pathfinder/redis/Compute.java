package dev.dqw4w9wgxcq.pathfinder.pathfinder.redis;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@FunctionalInterface
public interface Compute {
    String computeJson() throws ComputeException, FatalComputeException;

    @Accessors(fluent = true)
    class ComputeException extends Exception {
        @Getter
        private final int expire; // seconds

        public ComputeException(String message, int expire) {
            super(message);
            this.expire = expire;
        }

        public ComputeException(String message, Throwable cause, int expire) {
            super(message, cause);
            this.expire = expire;
        }
    }

    class FatalComputeException extends Exception {
        public FatalComputeException(String message) {
            super(message);
        }

        public FatalComputeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
