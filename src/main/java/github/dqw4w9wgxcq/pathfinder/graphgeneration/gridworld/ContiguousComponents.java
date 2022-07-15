package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Algo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public record ContiguousComponents(int[][] map, List<Integer> sizes) {
    public static List<ContiguousComponents> findInPlanes(TileGrid[] planes) {
        log.info("Finding contiguous components");
        return Arrays.stream(planes)
                .parallel()//uses too much mem
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
                var component = Algo.floodFill(new GridEdge(x, y, grid));
                log.debug("new component size {}", component.size());
                for (var edge : component) {
                    var gridEdge = ((GridEdge) edge);

                    var oldId = componentsMap[gridEdge.getX()][gridEdge.getY()];
                    if (componentsMap[gridEdge.getX()][gridEdge.getY()] != -1) {
                        throw new IllegalStateException("already assigned " + gridEdge + " oldId:" + oldId + " newId:" + id);
                    }

                    componentsMap[gridEdge.getX()][gridEdge.getY()] = id;
                }
                sizes.add(component.size());
                id++;
            }
        }

        log.info("Found {} components", sizes.size());
        log.info("smallest:{} largest:{} average:{} total:{}",
                sizes.stream().mapToInt(Integer::intValue).min().orElseThrow(),
                sizes.stream().mapToInt(Integer::intValue).max().orElseThrow(),
                sizes.stream().mapToInt(Integer::intValue).average().orElseThrow(),
                sizes.stream().mapToInt(Integer::intValue).sum());
        return new ContiguousComponents(componentsMap, sizes);
    }
}
