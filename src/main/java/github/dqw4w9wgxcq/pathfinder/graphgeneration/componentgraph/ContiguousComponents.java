package github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.TileFlags;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.TileGrid;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public record ContiguousComponents(int[][][] map, List<Integer> sizes) {
    public int count() {
        return sizes.size();
    }

    public static ContiguousComponents create(TileGrid[] planes) {
        var sizes = new ArrayList<Integer>();

        var map = new int[planes.length][planes[0].getSizeX()][planes[0].getSizeY()];
        for (var plane : map) {
            for (var row : plane) {
                Arrays.fill(row, -1);
            }
        }

        var id = 0;

        for (var z = 0; z < planes.length; z++) {
            var grid = planes[z];

            for (var x = 0; x < grid.getSizeX(); x++) {
                for (var y = 0; y < grid.getSizeY(); y++) {
                    if (map[z][x][y] != -1) {
                        continue;
                    }

                    if (!grid.checkFlag(x, y, TileFlags.HAVE_DATA)) {
                        continue;
                    }

                    if (grid.checkFlag(x, y, TileFlags.ANY_FULL)) {
                        continue;
                    }

                    log.debug("new component id:{} at x:{} y:{}", id, x, y);
                    var size = flood(map[z], grid, x, y, id);
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
        return new ContiguousComponents(map, sizes);
    }

    //in place algo cuts mem usage by ~4gb
    private static int flood(int[][] map, TileGrid grid, int startX, int startY, int id) {
        var frontier = new ArrayDeque<Point>();
        map[startX][startY] = id;
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
                        if (map[x][y] == -1) {
                            map[x][y] = id;
                            frontier.push(new Point(x, y));
                            size++;
                        } else {
                            assert map[x][y] == id : "tile already assigned to another component at x:" + x + "y:" + y + ".  means canTravelInDirection is not bidirectional somewhere";
                        }
                    }
                }
            }
        }

        return size;
    }
}