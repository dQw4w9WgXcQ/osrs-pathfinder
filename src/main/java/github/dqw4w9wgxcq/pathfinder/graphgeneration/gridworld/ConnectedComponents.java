package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Algo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public record ConnectedComponents(int[][] map, int count) {
    public static ConnectedComponents findIn(PlaneGrid grid) {
        var componentsMap = new int[grid.getSizeX()][grid.getSizeY()];
        for (var ys : componentsMap) {
            Arrays.fill(ys, -1);
        }

        var count = 0;

        for (var x = 0; x < grid.getSizeX(); x++) {
            for (var y = 0; y < grid.getSizeY(); y++) {
                if (componentsMap[x][y] != -1) {
                    continue;
                }

                if (!grid.checkFlag(x, y, CollisionFlags.VALID)) {
                    continue;
                }

                if (grid.checkFlag(x, y, CollisionFlags.ANY_FULL)) {
                    continue;
                }

                log.debug("new component id:{} at x:{} y:{}", count, x, y);
                var component = Algo.floodFill(new GridEdge(x, y, grid));
                log.debug("new component size {}", component.size());
                for (var edge : component) {
                    var gridEdge = ((GridEdge) edge);

                    componentsMap[gridEdge.x()][gridEdge.y()] = count;
                }
                count++;
            }
        }

        return new ConnectedComponents(componentsMap, count);
    }

    public static List<ConnectedComponents> findInPlanes(PlaneGrid[] planes) {
        var componentsPlanes = new ArrayList<ConnectedComponents>();
        for (var plane : planes) {
            componentsPlanes.add(findIn(plane));
        }

        return componentsPlanes;
    }
}
