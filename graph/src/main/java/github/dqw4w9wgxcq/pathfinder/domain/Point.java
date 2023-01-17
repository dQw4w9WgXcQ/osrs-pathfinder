package github.dqw4w9wgxcq.pathfinder.domain;

public record Point(int x, int y) {
    public Position toPosition(int plane) {
        return new Position(x, y, plane);
    }
}
