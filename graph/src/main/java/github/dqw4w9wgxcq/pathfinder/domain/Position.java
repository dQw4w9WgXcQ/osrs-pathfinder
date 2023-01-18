package github.dqw4w9wgxcq.pathfinder.domain;

public record Position(Point point, int plane) {
    public Position(int x, int y, int plane) {
        this(new Point(x, y), plane);
    }

    public int x() {
        return point.x();
    }

    public int y() {
        return point.y();
    }
}

