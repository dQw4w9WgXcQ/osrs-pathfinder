package github.dqw4w9wgxcq.pathfinder;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.Pathfinding;
import github.dqw4w9wgxcq.pathfinder.graph.store.GraphStore;
import github.dqw4w9wgxcq.pathfinder.graph.store.LinkStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws IOException {
        var dir = new File(System.getProperty("user.dir"));
        var linkStore = LinkStore.load(new FileInputStream(new File(dir, "links.zip")));
        var graphStore = GraphStore.load(new FileInputStream(new File(dir, "graph.zip")), linkStore.links());

        var pathfinding = Pathfinding.create(graphStore);

        var path = pathfinding.findPath(
                new Position(3232, 3232, 0),
                new Position(2441, 3088, 0),
                new Agent(0, Collections.emptyMap(), Collections.emptyList())
        );

        for (var step : path) {
            System.out.println(step);
        }
    }
}
