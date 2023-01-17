package github.dqw4w9wgxcq.pathfinder.graphgeneration.commons;

import github.dqw4w9wgxcq.pathfinder.domain.Position;

public class Util {
    public static final int REGION_SIZE = 64;

    public static int packRegionId(int x, int y) {
        return x << 8 | y;
    }

    public static int unpackRegionX(int id) {
        return id >> 8;
    }

    public static int unpackRegionY(int id) {
        return id & 0xFF;
    }

    public static Position fromRlPosition(net.runelite.cache.region.Position rl) {
        return new Position(rl.getX(), rl.getY(), rl.getZ());
    }
}
