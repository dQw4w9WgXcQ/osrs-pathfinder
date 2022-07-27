package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.RegionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is modeled after the collision map from the decompiled game client, but is global instead of scene.
 * uses bit flags to store collision data and bitwise operations to check for collisions
 */
@Slf4j
public class TileGrid {
    @Getter
    private final int sizeX, sizeY;

    private final int[][] tiles;

    TileGrid(int sizeX, int sizeY) {
        log.info("new TileGrid with size size x:" + sizeX + " y:" + sizeY);

        tiles = new int[sizeX][sizeY];

        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getConfig(int x, int y) {
        return tiles[x][y];
    }

    public boolean checkFlag(int x, int y, int mask) {
        return (tiles[x][y] & mask) != 0;
    }

    public boolean canTravelInDirection(int x, int y, int dx, int dy) {
        log.debug("canTravelInDirection x:{} y:{} dx:{} dy:{}", x, y, dx, dy);

        //assert over iae to disable for performance
        assert dx != 0 || dy != 0 : "dx and dy cant both be 0, found dx: " + dx + " dy: " + dy;
        assert Math.abs(dx) <= 1 && Math.abs(dy) <= 1 : "dx and dy must be 1 or -1, found dx: " + dx + " dy: " + dy;

        if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
            //todo can do diagonal movement in the future. need additional checks for diagonal collisions
            log.debug("diagonal movement, x:{} y:{} dx:{} dy:{}", x, y, dx, dy);
            return false;
        }

        var destinationX = x + dx;
        var destinationY = y + dy;

        if (destinationX < 0 || destinationX >= sizeX || destinationY < 0 || destinationY >= sizeY) {
            log.debug("destination out of bounds, x: " + x + " y: " + y + " dx: " + dx + " dy: " + dy);
            return false;
        }

        var config = tiles[x][y];
        var destinationConfig = tiles[x + dx][y + dy];

        var mask = 0;
        if (dx == 1) {
            mask |= TileFlags.E_WALL;

            if (dy == 1) {
                mask |= TileFlags.NE_WALL;
            } else if (dy == -1) {
                mask |= TileFlags.SE_WALL;
            }
        } else if (dx == -1) {
            mask |= TileFlags.W_WALL;

            if (dy == 1) {
                mask |= TileFlags.NW_WALL;
            } else if (dy == -1) {
                mask |= TileFlags.SW_WALL;
            }
        }

        if (dy == 1) {
            mask |= TileFlags.N_WALL;
        } else if (dy == -1) {
            mask |= TileFlags.S_WALL;
        }

        log.debug("check mask: {} stringed: {}config: {}", mask, TileFlags.describe(mask), TileFlags.describe(config));

        if ((config & mask) != 0) {
            log.debug("wall collision detected");
            return false;
        }

        if ((destinationConfig & TileFlags.ANY_FULL) != 0) {
            log.debug("full collision detected");
            return false;
        }

        if ((destinationConfig & TileFlags.HAVE_DATA) == 0) {
            log.debug("dont have data for destination");
            return false;
        }

        return true;
    }

    public void unmarkWall(int x, int y, Wall wall) {
        log.debug("unmarking wall at " + x + "," + y + " wall:" + wall);

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < sizeX && y < sizeY,
                "out of bounds: size:" + sizeX + "," + sizeY + ", point: " + x + "," + y
        );

        tiles[x][y] &= ~wall.getFlag();
        tiles[x + wall.getDx()][y + wall.getDy()] &= ~wall.oppositeFlag();
    }

    void markTile(int x, int y, int flag) {
        log.debug("marking flag " + flag + " " + x + "x " + y + "y");

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < sizeX && y < sizeY,
                "expected: x <" + sizeX + ", y <" + sizeY + ", found: " + x + "," + y + " flags: " + flag
        );

        var updatedConfig = tiles[x][y] |= flag;
        log.debug("updated config: {}", TileFlags.describe(updatedConfig));
    }

    void markHaveData(int regionX, int regionY) {
        var baseX = regionX * RegionUtil.SIZE;
        var baseY = regionY * RegionUtil.SIZE;
        for (var x = baseX; x < baseX + RegionUtil.SIZE; x++) {
            for (var y = baseY; y < baseY + RegionUtil.SIZE; y++) {
                markTile(x, y, TileFlags.HAVE_DATA);
            }
        }
    }

    void markAreaObject(int x, int y, int sizeX, int sizeY, boolean isFloorDecoration) {
        log.debug("marking game object at " + x + "," + y + " sizeX:" + sizeX + " sizeY:" + sizeY);

        Preconditions.checkArgument(
                sizeX >= 1 && sizeY >= 1,
                "expected: sizeXY >=1, found: " + sizeX + "," + sizeY
        );

        var flag = isFloorDecoration ? TileFlags.FLOOR_DECORATION : TileFlags.OBJECT;

        for (var i = x; i < x + sizeX; i++) {
            for (var j = y; j < y + sizeY; j++) {
                //todo, i think can fail if the object size goes off the map (will throw iae) but haven't seen it yet
                markTile(i, j, flag);
            }
        }
    }

    /**
     * adds flags for a wall object and opposing flags.
     * i.e. if a wall blocks movement west, then it will set flags on the west tile to block east
     */
    void markWallObject(int x, int y, int locationType, int orientation) {
        log.debug("marking wall object at " + x + "," + y + " locationType:" + locationType + " orientation:" + orientation);

        Preconditions.checkArgument(
                locationType >= 0 && locationType <= 3 && orientation >= 0 && orientation <= 3,
                "expected: 3 >= locationType >= 0, 3 >= orientation >= 0, found: x:%d y:%d locationType:%d orientation:%d", x, y, locationType, orientation
        );

        //copy-pasted from decompiled client
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
                //pillar
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

    @VisibleForTesting
    int[][] getTileArray() {
        return tiles;
    }

    @Override
    public String toString() {
        return "TileMap{" +
                "width=" + sizeX +
                ", height=" + sizeY +
                '}';
    }
}

//below are from decompiled game.  for reference
/*
public void addGameObject(int x, int y, int xSize, int ySize, boolean blocksLineOfSight) {
    int flag = 256;
    if (blocksLineOfSight) {
        flag += 131072;
    }

    x -= this.xInset;
    y -= this.yInset;

    for (int i = x; i < xSize + x; ++i) {
        if (i >= 0 && i < this.xSize) {
            for (int j = y; j < y + ySize; ++j) {
                if (j >= 0 && j < this.ySize) {
                    this.setFlag(i, j, flag);
                }
            }
        }
    }
}
 */
