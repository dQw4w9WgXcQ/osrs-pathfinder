package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graph.store.GraphStore;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
class Main {
    public static void main(String[] args) throws IOException {
        var graphStore = GraphStore.load(new File(System.getProperty("user.dir")));
        var componentGrid = new ComponentGrid(graphStore.planes());

        var linkDistanceCach = new LinkDistanceCache(componentGrid, graphStore.componentGraph());
        var localPathCache = new LocalPathCache(componentGrid);
        var pathfinding = new Pathfinding(componentGrid, graphStore.componentGraph(), linkDistanceCach, localPathCache);

        var startTime = System.currentTimeMillis();
        var path = pathfinding.findPath(
                new Position(3200, 3200, 0),
                new Position(3206, 3210, 1),
                new Agent(99, Map.of(), List.of())
        );
        var endTime = System.currentTimeMillis();
        var path2 = pathfinding.findPath(
                new Position(3200, 3200, 0),
                new Position(2442, 3087, 0),
                new Agent(99, Map.of(), List.of())
        );
        var endTime2 = System.currentTimeMillis();

        log.info("path: {}", path);
        log.info("time: {}ms", endTime - startTime);
        log.info("time2: {}ms", endTime2 - endTime);
    }
}
