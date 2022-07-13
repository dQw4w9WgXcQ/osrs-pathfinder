package github.dqw4w9wgxcq.pathfinder.graphgeneration.util;

public class RegionUtil {
    public static final int SIZE = 64;

    public static int packId(int x, int y) {
        return x << 8 | y;
    }

    public static int unpackX(int id) {
        return id >> 8;
    }

    public static int unpackY(int id) {
        return id & 0xFF;
    }
}
