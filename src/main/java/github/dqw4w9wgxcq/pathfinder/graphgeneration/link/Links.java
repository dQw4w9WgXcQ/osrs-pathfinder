package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair.StairLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair.StairLinks;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public record Links(List<DoorLink> doorLinks, List<StairLink> stairLinks) {
    public static Links create(CacheData cacheData, List<Location> objectLocations, ContiguousComponents contiguousComponents) {
        var doorLinks = DoorLinks.find(cacheData, objectLocations);
        var stairLinks = StairLinks.find(cacheData, objectLocations);

        for (var linkss : List.of(doorLinks, stairLinks)) {
            linkss.removeIf(link -> {
                var source = link.source();
                if (contiguousComponents.planes()[source.getZ()][source.getX()][source.getY()] == -1) {
                    log.info("removing link {} because source is not in a component", link);
                    return true;
                }

                var destination = link.destination();
                if (contiguousComponents.planes()[destination.getZ()][destination.getX()][destination.getY()] == -1) {
                    log.info("removing link {} because destination is not in a component", link);
                    return true;
                }

                return false;
            });
        }

        return new Links(doorLinks, stairLinks);
    }

    public List<Link> all() {
        var out = new ArrayList<Link>();
        out.addAll(doorLinks);
        out.addAll(stairLinks);
        return out;
    }
}
