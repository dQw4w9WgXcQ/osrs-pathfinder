package github.dqw4w9wgxcq.pathfinder.graphgeneration.util;

public class RegionUtil {
    public static final int SIZE = 64;

    public static int toId(int x, int y) {
        return x << 8 | y;
    }

    public static int toX(int id) {
        return id >> 8;
    }

    public static int toY(int id) {
        return id & 0xFF;
    }
}
