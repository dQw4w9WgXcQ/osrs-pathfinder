package dev.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import com.google.common.base.Preconditions;
import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import dev.dqw4w9wgxcq.pathfinder.commons.TileFlags;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @param planes A 3d array representing the tile grid.  Each value is the ID of the component that the tile belongs to.  Negative values are invalid (see comments).
 * @param sizes  Only used in tests.  The size (in tiles) of each component.  Component ID corresponds to index.
 */
@Slf4j
public record ContiguousComponents(int[][][] planes, List<Integer> sizes) {
    // https://i.imgur.com/LfKa5hz.png
    private static final int INVALID_REGION_X = 18;
    private static final int INVALID_REGION_Y = 39;

    public int count() {
        return sizes.size();
    }

    public static ContiguousComponents create(TileGrid[] gridPlanes) {
        log.info("Finding contiguous components");
        var startTime = System.currentTimeMillis();

        var sizes = new ArrayList<Integer>();

        var planes = new int[gridPlanes.length][gridPlanes[0].getWidth()][gridPlanes[0].getHeight()];
        for (var componentPlane : planes) {
            for (var row : componentPlane) {
                Arrays.fill(row, -1);
            }
        }

        // mark known invalid regions
        for (var z = 1; z < gridPlanes.length; z++) {
            floodfill(
                    planes[z],
                    gridPlanes[z],
                    INVALID_REGION_X * Constants.REGION_SIZE,
                    INVALID_REGION_Y * Constants.REGION_SIZE,
                    -2);
        }

        var id = 0;
        for (var z = 0; z < gridPlanes.length; z++) {
            var grid = gridPlanes[z];

            for (var x = 0; x < grid.getWidth(); x++) {
                for (var y = 0; y < grid.getHeight(); y++) {
                    if (planes[z][x][y] != -1) {
                        continue;
                    }

                    if (!grid.checkFlag(x, y, TileFlags.HAVE_DATA)) {
                        continue;
                    }

                    if (grid.checkFlag(x, y, TileFlags.ANY_FULL)) {
                        continue;
                    }

                    log.debug("new component id:{} at x:{} y:{}", id, x, y);
                    var size = floodfill(planes[z], grid, x, y, id);
                    log.debug("new component id:{} size:{}", id, size);

                    sizes.add(size);

                    id++;
                }
            }
        }

        // mark invalid regions back to -1
        for (var z = 1; z < gridPlanes.length; z++) {
            floodfill(
                    planes[z],
                    gridPlanes[z],
                    INVALID_REGION_X * Constants.REGION_SIZE,
                    INVALID_REGION_Y * Constants.REGION_SIZE,
                    -1);
        }

        var finishTime = System.currentTimeMillis();
        log.info(
                "Found {} components smallest:{} largest:{} average:{} total:{} in {}ms",
                sizes.size(),
                sizes.stream().mapToInt(Integer::intValue).min().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).max().orElse(0),
                (int) sizes.stream().mapToInt(Integer::intValue).average().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).sum(),
                finishTime - startTime);
        return new ContiguousComponents(planes, sizes);
    }

    // in place algo cuts mem usage by ~4gb
    private static int floodfill(int[][] plane, TileGrid grid, int startX, int startY, int id) {
        var oldId = plane[startX][startY];
        Preconditions.checkState(oldId != id, "oldId{} == id{}", oldId, id);

        plane[startX][startY] = id;
        var frontier = new ArrayDeque<Point>();
        frontier.add(new Point(startX, startY));
        var size = 1;
        // dfs
        while (!frontier.isEmpty()) {
            var edge = frontier.pop();
            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    if (dx != 0 && dy != 0) { // no diagonals
                        continue;
                    }

                    if (grid.canTravelInDirection(edge.x(), edge.y(), dx, dy)) {
                        var x = edge.x() + dx;
                        var y = edge.y() + dy;
                        if (plane[x][y] == oldId) {
                            plane[x][y] = id;
                            frontier.push(new Point(x, y));
                            size++;
                        } else {
                            assert plane[x][y] == id
                                    : "tile already assigned to another component at x:" + x + "y:" + y
                                            + ".  means canTravelInDirection is not bidirectional somewhere";
                        }
                    }
                }
            }
        }

        return size;
    }
}
