package dev.dqw4w9wgxcq.pathfinder.commons.domain;

public record Position(int x, int y, int plane) {
    public Point toPoint() {
        return new Point(x, y);
    }
}