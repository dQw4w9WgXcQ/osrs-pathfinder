package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Graph() {
    public static Graph generate(CacheData data) {
        //var gridWorld = GridWorld.create(data.regionData(), data.objectData());

        //var componentsPlanes = ContiguousComponents.findInPlanes(gridWorld.getPlanes());

        var doorLinks = DoorLinks.find(data.regionData(), data.objectData());

        return new Graph();
    }
}
