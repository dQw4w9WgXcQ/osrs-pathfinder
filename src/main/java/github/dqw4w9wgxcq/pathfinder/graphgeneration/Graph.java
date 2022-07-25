package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Position;

import java.util.List;
import java.util.Map;

@Slf4j
public record Graph(List<ContiguousComponents> components) {
    public static Graph generate(CacheData data) {
//        var gridWorld = GridWorld.create(data.regionData(), data.objectData());

        //var components = ContiguousComponents.findIn(gridWorld);

        var doorLinks = DoorLinks.find(data.regionData(), data.objectData());

        for (var positionDoorLinkEntry : doorLinks.entrySet()) {
            var position = positionDoorLinkEntry.getKey();
            var doorLink = positionDoorLinkEntry.getValue();
            System.out.printf("Found door link from:%s: %s\n", position, doorLink);
        }

        //PlaneLinks.find(data.regionData(), data.objectData());

        //return new Graph(components);
        throw new UnsupportedOperationException("not implemented");//todo
    }
}
