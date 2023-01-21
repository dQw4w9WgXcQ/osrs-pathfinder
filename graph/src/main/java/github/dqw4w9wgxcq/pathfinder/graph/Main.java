package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graph.domain.PathStep;
import github.dqw4w9wgxcq.pathfinder.graph.store.GraphStore;
import github.dqw4w9wgxcq.pathfinder.graph.store.LinkStore;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
class Main {
    public static void main(String[] args) throws IOException {
        var linkStore = LinkStore.load(new File(System.getProperty("user.dir")));
        var graphStore = GraphStore.load(new File(System.getProperty("user.dir")), linkStore.links());
        var componentGrid = new ComponentGrid(graphStore.planes());

        var linkDistanceCach = new LinkDistanceCache(componentGrid, graphStore.componentGraph());
        var localPathCache = new LocalPathCache(componentGrid);
        var pathfinding = new Pathfinding(componentGrid, graphStore.componentGraph(), linkDistanceCach, localPathCache);

        log.info("ree");

        var startTime = System.currentTimeMillis();
        var path1 = pathfinding.findPath(
                new Position(3200, 3200, 0),
                new Position(2442, 3087, 0),
                new Agent(99, Map.of(), List.of())
        );
        var endTime = System.currentTimeMillis();
        log.info("path1: {} in {}ms", pathToString(path1), endTime - startTime);
        var path2 = pathfinding.findPath(
                new Position(3200, 3200, 0),
                new Position(3208, 3214, 1),
                new Agent(99, Map.of(), List.of())
        );
        var endTime2 = System.currentTimeMillis();
        log.info("path2: {} in {}ms", pathToString(path2), endTime2 - endTime);
    }

    public static String pathToString(List<PathStep> path) {
        if (path == null) return null;

        var sb = new StringBuilder();
        sb.append("\n");
        for (var step : path) {
            sb.append(step).append("\n");
        }
        return sb.toString();
    }
}
