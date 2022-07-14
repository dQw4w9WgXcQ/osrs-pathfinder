package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ConnectedComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;

import java.io.File;
import java.io.IOException;

public class PathfinderGraph {
    public void save(File dir) throws IOException {
        throw new UnsupportedOperationException("Not implemented");//todo
    }

    public static PathfinderGraph generate(CacheData cacheData) {
        var out = new PathfinderGraph();

        var world = GridWorld.create(cacheData);

        var componentsPlanes = ConnectedComponents.findInPlanes(world.getPlanes());



        return out;
    }
}
