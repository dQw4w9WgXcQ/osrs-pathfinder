package github.dqw4w9wgxcq.pathfinder.graph.domain;

public record Position(int x, int y, int plane) {
    public Point toPoint() {
        return new Point(x, y);
    }
}
