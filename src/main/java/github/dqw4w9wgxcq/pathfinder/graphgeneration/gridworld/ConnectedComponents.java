package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Algo;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public record ConnectedComponents(int count, int[][] map) {
    public static ConnectedComponents findIn(TileGrid grid) {
        var componentsMap = new int[grid.getWidth()][grid.getHeight()];
        for (var ys : componentsMap) {
            Arrays.fill(ys, -1);
        }

        var count = 0;

        for (var x = 0; x < grid.getWidth(); x++) {
            for (var y = 0; y < grid.getHeight(); y++) {
                if (componentsMap[x][y] == -1) {
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
        }

        return new ConnectedComponents(count, componentsMap);
    }
}
