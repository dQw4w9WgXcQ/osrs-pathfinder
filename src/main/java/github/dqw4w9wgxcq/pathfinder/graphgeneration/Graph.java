package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public record Graph(List<ContiguousComponents> components) {
    public static Graph generate(CacheData data) {
        var world = GridWorld.create(data.regionData(), data.objectData());

        var componentsPlanes = ContiguousComponents.findIn(world);

        var doorLinks = DoorLinks.find(data.regionData(), data.objectData());

        doorLinks = DoorLinks.removeInterComponentDoorsFromWorld(doorLinks, componentsPlanes, world);



        return new Graph(componentsPlanes);
    }
}
