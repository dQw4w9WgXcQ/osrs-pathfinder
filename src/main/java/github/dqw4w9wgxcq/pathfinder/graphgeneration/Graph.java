package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import lombok.Getter;

public class Graph {
    @Getter
    private final int[][][] worldTileFlags;

    private Graph(int highestRegionX, int highestRegionY) {
        var worldXSize = (highestRegionX + 1) * 64;
        var worldYSize = (highestRegionY + 1) * 64;
        worldTileFlags = new int[4][worldXSize][worldYSize];
    }

    public void addTileFlag(int plane, int x, int y, int flag) {
        worldTileFlags[plane][x][y] |= (flag & MovementFlags.VISITED);
    }

    public static Graph generate(CacheData data) {
        var regionLoader = data.getRegionLoader();
        var objectManager = data.getObjectManager();

        var graph = new Graph(regionLoader.getHighestX().getRegionX(), regionLoader.getHighestY().getRegionY());

        throw new UnsupportedOperationException("TODO");//todo
    }
}