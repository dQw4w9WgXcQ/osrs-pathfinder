package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public record Graph(List<ContiguousComponents> components) {
    public static Graph generate(CacheData data) {
        var objectLocations = get0x2AdjustedLocations(data.regionData().regions());

        var world = GridWorld.create(data.regionData(), data.objectData(), objectLocations);

        var components = ContiguousComponents.findIn(world);

        var doorLinks = DoorLinks.find(objectLocations, data.objectData().definitions());

        doorLinks = DoorLinks.removeInterComponentDoorsFromWorld(doorLinks, components, world);


        return new Graph(components);
    }

    /**
     * 0x2 is a render flag used for bridges and multi-level buildings.  when the flag is present, objects from the plane above affect the collision map of the plane below.
     */
    private static List<Location> get0x2AdjustedLocations(Collection<Region> regions) {
        var locations = new ArrayList<Location>();
        for (var region : regions) {
            for (var location : region.getLocations()) {
                var position = location.getPosition();
                var plane = position.getZ();
                if ((region.getTileSetting(1, position.getX() - region.getBaseX(), position.getY() - region.getBaseY()) & 0x2) == 0x2) {
                    log.debug("location is 0x2: {}", location);
                    plane--;
                }

                var adjLocation = new Location(
                        location.getId(),
                        location.getType(),
                        location.getOrientation(),
                        new Position(position.getX(), position.getY(), plane)
                );

                if (plane < 0) {
                    log.debug("0x2 location is below 0: {}", location);
                    continue;
                }

                locations.add(adjLocation);
            }
        }

        return locations;
    }
}
