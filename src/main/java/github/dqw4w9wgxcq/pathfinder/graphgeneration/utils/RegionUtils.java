package github.dqw4w9wgxcq.pathfinder.graphgeneration.utils;

public class RegionUtils {
    public static final int SIZE = 64;

    public static int toId(int x, int y) {
        return x << 8 | y;
    }
}
