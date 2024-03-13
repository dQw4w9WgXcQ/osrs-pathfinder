package dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import com.google.common.base.Preconditions;
import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import dev.dqw4w9wgxcq.pathfinder.commons.TileFlags;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Based off code from the decompiled game client.  Uses bitwise.
 */
@Slf4j
public class TileGrid {
    @Getter
    private final int width, height;

    private final int[][] tiles;

    @VisibleForTesting
    public TileGrid(int width, int height) {
        log.debug("new TileGrid " + width + " x " + height);
        this.width = width;
        this.height = height;

        tiles = new int[width][height];
    }

    public int getConfig(int x, int y) {
        return tiles[x][y];
    }

    public boolean checkFlag(int x, int y, int mask) {
        return (tiles[x][y] & mask) != 0;
    }

    @VisibleForTesting
    public void markTile(int x, int y, int flag) {
        log.debug("marking flag " + flag + " " + x + "x " + y + "y");

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < width && y < height,
                "expected: x <" + width + ", y <" + height + ", found: " + x + "," + y + " flags: " + flag);

        var updatedConfig = tiles[x][y] |= flag;
        log.debug("updated config: {}", TileFlags.describe(updatedConfig));
    }

    @VisibleForTesting
    public void unmarkTile(int x, int y, int flag) {
        log.debug("unmarking flag " + flag + " " + x + "x " + y + "y");

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < width && y < height,
                "expected: x <" + width + ", y <" + height + ", found: " + x + "," + y + " flags: " + flag);

        var updatedConfig = tiles[x][y] &= ~flag;
        log.debug("updated config: {}", TileFlags.describe(updatedConfig));
    }

    @VisibleForTesting
    public void markRegionHaveData(int regionX, int regionY) {
        var baseX = regionX * Constants.REGION_SIZE;
        var baseY = regionY * Constants.REGION_SIZE;
        for (var x = baseX; x < baseX + Constants.REGION_SIZE; x++) {
            for (var y = baseY; y < baseY + Constants.REGION_SIZE; y++) {
                markTile(x, y, TileFlags.HAVE_DATA);
            }
        }
    }

    // floor decoration/object distinction is never really used
    @VisibleForTesting
    public void markObject(int x, int y, int sizeX, int sizeY, boolean isFloorDecoration) {
        log.debug("marking game object at " + x + "," + y + " sizeX:" + sizeX + " sizeY:" + sizeY);

        Preconditions.checkArgument(sizeX >= 1 && sizeY >= 1, "expected: sizeXY >=1, found: " + sizeX + "," + sizeY);

        var flag = isFloorDecoration ? TileFlags.FLOOR_DECORATION : TileFlags.OBJECT;

        for (var i = x; i < x + sizeX; i++) {
            for (var j = y; j < y + sizeY; j++) {
                // todo I think this can fail if an object size goes off the map, but haven't seen it yet (markTile will
                // throw illegal args)
                markTile(i, j, flag);
            }
        }
    }

    /**
     * adds flags for a wall object and opposing flags.
     * i.e. if a wall blocks movement west, then it will set flags on the west tile to block east
     */
    void markWallObject(int x, int y, int locationType, int orientation) {
        log.debug("marking wall object at " + x + "," + y + " locationType:" + locationType + " orientation:"
                + orientation);

        Preconditions.checkArgument(
                locationType >= 0 && locationType <= 3 && orientation >= 0 && orientation <= 3,
                "expected: 3 >= locationType >= 0, 3 >= orientation >= 0, found: x:%d y:%d locationType:%d orientation:%d",
                x,
                y,
                locationType,
                orientation);

        // copy-pasted from decompiled client
        switch (locationType) {
            case 0 -> {
                switch (orientation) {
                    case 0 -> {
                        markTile(x, y, TileFlags.W_WALL);
                        markTile(x - 1, y, 8);
                    }
                    case 1 -> {
                        markTile(x, y, TileFlags.N_WALL);
                        markTile(x, y + 1, 32);
                    }
                    case 2 -> {
                        markTile(x, y, TileFlags.E_WALL);
                        markTile(x + 1, y, 128);
                    }
                    case 3 -> {
                        markTile(x, y, TileFlags.S_WALL);
                        markTile(x, y - 1, 2);
                    }
                }
            }
            case 1, 3 -> {
                // pillar
                switch (orientation) {
                    case 0 -> {
                        markTile(x, y, TileFlags.NW_WALL);
                        markTile(x - 1, y + 1, 16);
                    }
                    case 1 -> {
                        markTile(x, y, TileFlags.NE_WALL);
                        markTile(x + 1, y + 1, 64);
                    }
                    case 2 -> {
                        markTile(x, y, TileFlags.SE_WALL);
                        markTile(x + 1, y - 1, 1);
                    }
                    case 3 -> {
                        markTile(x, y, TileFlags.SW_WALL);
                        markTile(x - 1, y - 1, 4);
                    }
                }
            }
            case 2 -> {
                switch (orientation) {
                    case 0 -> {
                        markTile(x, y, TileFlags.N_WALL | TileFlags.W_WALL);
                        markTile(x - 1, y, 8);
                        markTile(x, y + 1, 32);
                    }
                    case 1 -> {
                        markTile(x, y, TileFlags.N_WALL | TileFlags.E_WALL);
                        markTile(x, y + 1, 32);
                        markTile(x + 1, y, 128);
                    }
                    case 2 -> {
                        markTile(x, y, TileFlags.S_WALL | TileFlags.E_WALL);
                        markTile(x + 1, y, 128);
                        markTile(x, y - 1, 2);
                    }
                    case 3 -> {
                        markTile(x, y, TileFlags.S_WALL | TileFlags.W_WALL);
                        markTile(x, y - 1, 2);
                        markTile(x - 1, y, 8);
                    }
                }
            }
        }
    }

    public boolean canTravelInDirection(int x, int y, int dx, int dy) {
        log.debug("canTravelInDirection x:{} y:{} dx:{} dy:{}", x, y, dx, dy);

        Preconditions.checkArgument(dx != 0 || dy != 0, "both 0 dx: " + dx + " dy: " + dy);
        Preconditions.checkArgument(
                Math.abs(dx) <= 1 && Math.abs(dy) <= 1, "dx and dy must be 1 to -1, found dx: " + dx + " dy: " + dy);

        var config = tiles[x][y];

        var mask = 0;
        var opposingXMask = TileFlags.ANY_FULL;
        var opposingYMask = TileFlags.ANY_FULL;
        var opposingXYMask = TileFlags.ANY_FULL;
        if (dx == 1) {
            mask |= TileFlags.E_WALL;
            opposingXMask |= TileFlags.W_WALL;
            opposingXYMask |= TileFlags.W_WALL;

            if (dy == 1) {
                mask |= TileFlags.NE_WALL;
                opposingXYMask |= TileFlags.SW_WALL;
            } else if (dy == -1) {
                mask |= TileFlags.SE_WALL;
                opposingXYMask |= TileFlags.NW_WALL;
            }
        } else if (dx == -1) {
            mask |= TileFlags.W_WALL;
            opposingXMask |= TileFlags.E_WALL;
            opposingXYMask |= TileFlags.E_WALL;

            if (dy == 1) {
                mask |= TileFlags.NW_WALL;
                opposingXYMask |= TileFlags.SE_WALL;
            } else if (dy == -1) {
                mask |= TileFlags.SW_WALL;
                opposingXYMask |= TileFlags.NE_WALL;
            }
        }

        if (dy == 1) {
            mask |= TileFlags.N_WALL;
            opposingYMask |= TileFlags.S_WALL;
            opposingXYMask |= TileFlags.S_WALL;
        } else if (dy == -1) {
            mask |= TileFlags.S_WALL;
            opposingYMask |= TileFlags.N_WALL;
            opposingXYMask |= TileFlags.N_WALL;
        }

        log.debug(
                "check mask: {} desc: {} config desc: {}", mask, TileFlags.describe(mask), TileFlags.describe(config));

        if ((config & mask) != 0) {
            log.debug("wall collision detected");
            return false;
        }

        var endX = x + dx;
        var endY = y + dy;
        if (endX < 0 || endX >= width || endY < 0 || endY >= height) {
            log.debug("end out of bounds");
            return false;
        }

        if (dx != 0) {
            var adjacentXConfig = tiles[endX][y];
            if ((adjacentXConfig & TileFlags.HAVE_DATA) == 0) {
                log.debug("dont have data for adjacent x");
                return false;
            }

            if ((adjacentXConfig & opposingXMask) != 0) {
                log.debug("opposing x wall collision detected");
                return false;
            }
        }

        if (dy != 0) {
            var adjacentYConfig = tiles[x][endY];
            if ((adjacentYConfig & TileFlags.HAVE_DATA) == 0) {
                log.debug("dont have data for adjacent y");
                return false;
            }

            if ((adjacentYConfig & opposingYMask) != 0) {
                log.debug("opposing y wall collision detected");
                return false;
            }
        }

        if (dx != 0 && dy != 0) {
            var diagonalConfig = tiles[endX][endY];
            if ((diagonalConfig & TileFlags.HAVE_DATA) == 0) {
                log.debug("dont have data for diagonal");
                return false;
            }

            if ((diagonalConfig & opposingXYMask) != 0) {
                log.debug("opposing xy wall collision detected");
                return false;
            }
        }

        return true;
    }

    public PathfindingGrid toPathfindingGrid(int[][] componentIds) {
        var grid = new byte[width][height];

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (ComponentGrid.isBlocked(componentIds, x, y)) {
                    continue;
                }

                if (!checkFlag(x, y, TileFlags.HAVE_DATA)) {
                    continue;
                }

                if (checkFlag(x, y, TileFlags.ANY_FULL)) {
                    continue;
                }

                if (canTravelInDirection(x, y, 0, 1)) {
                    grid[x][y] |= PathfindingGrid.NORTH;
                }

                if (canTravelInDirection(x, y, 0, -1)) {
                    grid[x][y] |= PathfindingGrid.SOUTH;
                }

                if (canTravelInDirection(x, y, 1, 0)) {
                    grid[x][y] |= PathfindingGrid.EAST;
                }

                if (canTravelInDirection(x, y, -1, 0)) {
                    grid[x][y] |= PathfindingGrid.WEST;
                }

                if (canTravelInDirection(x, y, 1, 1)) {
                    grid[x][y] |= PathfindingGrid.NORTH_EAST;
                }

                if (canTravelInDirection(x, y, -1, 1)) {
                    grid[x][y] |= PathfindingGrid.NORTH_WEST;
                }

                if (canTravelInDirection(x, y, 1, -1)) {
                    grid[x][y] |= PathfindingGrid.SOUTH_EAST;
                }

                if (canTravelInDirection(x, y, -1, -1)) {
                    grid[x][y] |= PathfindingGrid.SOUTH_WEST;
                }
            }
        }

        return new PathfindingGrid(grid);
    }

    @VisibleForTesting
    public int[][] getTileArray() {
        return tiles;
    }
}
