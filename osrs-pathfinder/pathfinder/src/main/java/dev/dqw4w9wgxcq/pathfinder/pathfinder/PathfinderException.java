package dev.dqw4w9wgxcq.pathfinder.pathfinder;

public class PathfinderException extends Exception {
    PathfinderException(String message) {
        super(message);
    }

    PathfinderException(String message, Throwable cause) {
        super(message, cause);
    }

    PathfinderException(Throwable cause) {
        super(cause);
    }
}
