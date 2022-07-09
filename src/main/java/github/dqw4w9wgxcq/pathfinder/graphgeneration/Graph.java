package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.Edge;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.doors.DoorsStep;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.dungeons.DungeonsStep;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.floors.FloorsStep;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.objectspawns.ObjectSpawnsStep;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.stairs.StairsStep;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    @Getter
    private final int[][][] worldTileFlags;
    @Getter
    private final Map<WorldPoint, Edge> links = new HashMap<>();

    private Graph(int highestRegionX, int highestRegionY) {
        int worldXSize = (highestRegionX + 1) * 64;
        int worldYSize = (highestRegionY + 1) * 64;
        worldTileFlags = new int[3][worldXSize][worldYSize];
    }

    public void addLink(int plane, int x, int y, Edge edge) {
        links.put(new WorldPoint(x, y, plane), edge);
    }

    public void addTileFlag(int plane, int x, int y, int flag) {
        worldTileFlags[plane][x][y] |= (flag & TileFlags.VISITED);
    }

    public void write(File outFile) {
        throw new UnsupportedOperationException("TODO");//todo
    }

    public static Graph generate(CacheData data) {
        var regionLoader = data.getRegionLoader();
        var objectManager = data.getObjectManager();

        var graph = new Graph(regionLoader.getHighestX().getBaseX(), regionLoader.getHighestY().getBaseY());

        new ObjectSpawnsStep(regionLoader, objectManager).accept(graph);
        new FloorsStep(regionLoader).accept(graph);
        new DoorsStep(regionLoader, objectManager).accept(graph);
        new StairsStep(regionLoader, objectManager).accept(graph);
        new DungeonsStep(regionLoader, objectManager).accept(graph);
        //new IntraMapLinksStep(regionLoader, objectManager, scriptLoader).accept(graph);

        return graph;
    }
}