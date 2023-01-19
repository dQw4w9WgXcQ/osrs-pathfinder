package github.dqw4w9wgxcq.pathfinder.graphgeneration.commons;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;
import org.jetbrains.annotations.Nullable;

@Slf4j
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

    public static @Nullable net.runelite.cache.region.Position findNotBlockedAdjacent(TileWorld tileWorld, net.runelite.cache.region.Position position, int sizeX, int sizeY) {
        TileGrid grid = tileWorld.getPlanes()[position.getZ()];

        for (int i = 0; i < sizeX; i++) {
            var x = position.getX() + i;
            int y;
            if (grid.canTravelInDirection(x, position.getY() + sizeY, 0, 1)) {
                y = position.getY() + 1;
            } else if (grid.canTravelInDirection(x, position.getY(), 0, -1)) {
                y = position.getY() - 1;
            } else {
                continue;
            }

            return new net.runelite.cache.region.Position(x, y, position.getZ());
        }

        for (int i = 0; i < sizeY; i++) {
            var y = position.getY() + i;
            int x;
            if (grid.canTravelInDirection(position.getX() + sizeX, y, 1, 0)) {
                x = position.getX() + 1;
            } else if (grid.canTravelInDirection(position.getX(), y, -1, 0)) {
                x = position.getX() - 1;
            } else {
                continue;
            }

            return new net.runelite.cache.region.Position(x, y, position.getZ());
        }

        return null;
    }

    //https://discord.com/channels/167513997694861313/167513997694861313/1051576900188909619
    public static Direction getInteractDirection(int blockingMask) {
        if (blockingMask > 0) {//idk if this is necessary
            if ((blockingMask & 0x8) == 0) {
                return Direction.WEST;
            }
            if ((blockingMask & 0x2) == 0) {
                return Direction.EAST;
            }
            if ((blockingMask & 0x4) == 0) {
                return Direction.SOUTH;
            }
            if ((blockingMask & 0x1) == 0) {
                return Direction.NORTH;
            }
        }

        return null;
    }
}
