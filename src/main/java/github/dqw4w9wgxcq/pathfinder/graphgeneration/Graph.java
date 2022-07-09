package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import lombok.Getter;

public class Graph {
    @Getter
    private final int[][][] worldTileFlags;

    public Graph(CacheData data) {
        var regionLoader = data.getRegionLoader();



        var objectManager = data.getObjectManager();

        throw new UnsupportedOperationException("TODO");//todo
    }


}