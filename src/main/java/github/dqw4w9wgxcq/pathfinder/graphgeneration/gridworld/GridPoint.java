package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

public record GridPoint(int x, int y) {
    public int getId() {
        return toId(x, y);
    }

    public static int toId(int x, int y) {
        return x << 16 | y;
    }

    public static GridPoint fromId(int id) {
        return new GridPoint(id >> 16, id & 0xFFFF);
    }
}
