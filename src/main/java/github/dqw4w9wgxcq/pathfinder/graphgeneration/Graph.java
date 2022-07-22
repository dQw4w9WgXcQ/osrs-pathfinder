package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public record Graph(List<ContiguousComponents> components) {
    public static Graph generate(CacheData data) {
        var gridWorld = GridWorld.create(data.regionData(), data.objectData());

        var components = ContiguousComponents.findIn(gridWorld);

        //var doorLinks = DoorLinks.find(data.regionData(), data.objectData());

        //PlaneLinks.find(data.regionData(), data.objectData());

        return new Graph(components);
    }
}
