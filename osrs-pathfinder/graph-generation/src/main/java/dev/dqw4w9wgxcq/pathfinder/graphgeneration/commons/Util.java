package dev.dqw4w9wgxcq.pathfinder.graphgeneration.commons;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
public class Util {
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

    public static List<Position> findNotBlockedAdjacent(TileWorld tileWorld, Position position, int sizeX, int sizeY) {
        var grid = tileWorld.getPlanes()[position.plane()];

        var adjacent = new ArrayList<Position>();
        for (int i = 0; i < sizeX; i++) {
            int x = position.x() + i;
            int y;
            if (grid.canTravelInDirection(x, position.y() + sizeY - 1, 0, 1)) {
                y = position.y() + sizeY;
            } else if (grid.canTravelInDirection(x, position.y(), 0, -1)) {
                y = position.y() - 1;
            } else {
                continue;
            }

            adjacent.add(new Position(x, y, position.plane()));
        }

        for (int i = 0; i < sizeY; i++) {
            int x;
            int y = position.y() + i;
            if (grid.canTravelInDirection(position.x() + sizeX - 1, y, 1, 0)) {
                x = position.x() + sizeX;
            } else if (grid.canTravelInDirection(position.x(), y, -1, 0)) {
                x = position.x() - 1;
            } else {
                continue;
            }

            adjacent.add(new Position(x, y, position.plane()));
        }

        return adjacent;
    }

    // https://discord.com/channels/167513997694861313/167513997694861313/1051576900188909619
    public static Direction getInteractDirection(int blockingMask) {
        if (blockingMask > 0) { // idk if this is necessary
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
