package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public record ContiguousComponents(int[][] map, List<Integer> sizes) {
    public static List<ContiguousComponents> findIn(GridWorld gridWorld) {
        log.info("Finding contiguous components");
        return Arrays.stream(gridWorld.getPlanes())
                .parallel()
                .map(ContiguousComponents::findIn)
                .toList();
    }

    @VisibleForTesting
    static ContiguousComponents findIn(TileGrid grid) {
        var componentsMap = new int[grid.getSizeX()][grid.getSizeY()];
        for (var column : componentsMap) {
            Arrays.fill(column, -1);
        }

        var sizes = new ArrayList<Integer>();

        var id = 0;

        for (var x = 0; x < grid.getSizeX(); x++) {
            for (var y = 0; y < grid.getSizeY(); y++) {
                if (componentsMap[x][y] != -1) {
                    continue;
                }

                if (!grid.checkFlag(x, y, TileFlags.HAVE_DATA)) {
                    continue;
                }

                if (grid.checkFlag(x, y, TileFlags.ANY_FULL)) {
                    continue;
                }

                log.debug("new component id:{} at x:{} y:{}", id, x, y);
                var size = flood(componentsMap, grid, x, y, id);
                log.debug("new component id:{} size:{}", id, size);

                sizes.add(size);

                id++;
            }
        }

        log.info("Found {} components", sizes.size());
        log.info("smallest:{} largest:{} average:{} total:{}",
                sizes.stream().mapToInt(Integer::intValue).min().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).max().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).average().orElse(0),
                sizes.stream().mapToInt(Integer::intValue).sum());
        return new ContiguousComponents(componentsMap, sizes);
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

                    if (grid.canTravelInDirection(edge.x(), edge.y(), dx, dy)) {
                        var x = edge.x() + dx;
                        var y = edge.y() + dy;
                        if (map[x][y] == -1) {
                            map[x][y] = id;
                            frontier.push(new Point(x, y));
                            size++;
                        } else {
                            assert map[x][y] == id;//means canTravelInDirection is not bidirectional somewhere
                        }
                    }
                }
            }
        }

        return size;
    }
}
