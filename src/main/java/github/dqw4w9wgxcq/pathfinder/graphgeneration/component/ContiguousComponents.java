package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileFlags;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

//index of sizes is the ID of the component (values in the map array)
@Slf4j
public record ContiguousComponents(int[][][] planes, ArrayList<Integer> sizes) {
    public int count() {
        return sizes.size();
    }

    public static ContiguousComponents create(TileGrid[] gridPlanes) {
        var sizes = new ArrayList<Integer>();

        var componentsPlanes = new int[gridPlanes.length][gridPlanes[0].getSizeX()][gridPlanes[0].getSizeY()];
        for (var componentPlane : componentsPlanes) {
            for (var row : componentPlane) {
                Arrays.fill(row, -1);
            }
        }

        var id = 0;
        for (var z = 0; z < gridPlanes.length; z++) {
            var grid = gridPlanes[z];

            for (var x = 0; x < grid.getSizeX(); x++) {
                for (var y = 0; y < grid.getSizeY(); y++) {
                    if (componentsPlanes[z][x][y] != -1) {
                        continue;
                    }

                    if (!grid.checkFlag(x, y, TileFlags.HAVE_DATA)) {
                        continue;
                    }

                    if (grid.checkFlag(x, y, TileFlags.ANY_FULL)) {
                        continue;
                    }

                    log.debug("new component id:{} at x:{} y:{}", id, x, y);
                    var size = floodfill(componentsPlanes[z], grid, x, y, id);
                    log.debug("new component id:{} size:{}", id, size);

                    sizes.add(size);

                    id++;
                }
            }
        }

        log.info("Found {} components", sizes.size());
        log.info("smallest:{} largest:{} average:{} total:{}",
                sizes.stream().mapToInt(Integer::intValue).min().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).max().orElse(0),
                (int) sizes.stream().mapToInt(Integer::intValue).average().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).sum());
        return new ContiguousComponents(componentsPlanes, sizes);
    }

    //in place algo cuts mem usage by ~4gb
    private static int floodfill(int[][] plane, TileGrid grid, int startX, int startY, int id) {
        var frontier = new ArrayDeque<Point>();
        plane[startX][startY] = id;
        frontier.add(new Point(startX, startY));
        var size = 1;
        //dfs
        while (!frontier.isEmpty()) {
            var edge = frontier.pop();
            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    if (dx != 0 && dy != 0) {//no diagonals
                        continue;
                    }

                    if (grid.canTravelInDirection(edge.x(), edge.y(), dx, dy)) {
                        var x = edge.x() + dx;
                        var y = edge.y() + dy;
                        if (plane[x][y] == -1) {
                            plane[x][y] = id;
                            frontier.push(new Point(x, y));
                            size++;
                        } else {
                            assert plane[x][y] == id : "tile already assigned to another component at x:" + x + "y:" + y + ".  means canTravelInDirection is not bidirectional somewhere";
                        }
                    }
                }
            }
        }

        return size;
    }
}
