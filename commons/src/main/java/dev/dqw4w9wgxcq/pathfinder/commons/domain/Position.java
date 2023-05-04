package dev.dqw4w9wgxcq.pathfinder.commons.domain;

public record Position(int x, int y, int plane) {
    public Point point() {
        return new Point(x, y);
    }
}

