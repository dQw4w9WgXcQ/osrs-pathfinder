package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graph.Links;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;

import java.util.List;

@Slf4j
public class FindLinks {
    public static Links find(CacheData cacheData, List<Location> objectLocations, ContiguousComponents contiguousComponents) {
        log.info("finding links");

        var doorLinks = DoorLinks.find(cacheData, objectLocations);
        var stairLinks = StairLinks.find(cacheData, objectLocations);
        var undergroundLinks = UndergroundLinks.find(cacheData, objectLocations);
        var specialLinks = SpecialLinks.find();

        for (var links : List.of(doorLinks, stairLinks, specialLinks)) {
            links.removeIf(link -> {
                var source = link.origin();
                if (contiguousComponents.planes()[source.plane()][source.x()][source.y()] == -1) {
                    log.debug("removing link {} because source is not in a component", link);
                    return true;
                }

                var destination = link.destination();
                if (contiguousComponents.planes()[destination.plane()][destination.x()][destination.y()] == -1) {
                    log.debug("removing link {} because destination is not in a component", link);
                    return true;
                }

                return false;
            });
        }

        log.info("found {} links", doorLinks.size() + specialLinks.size() + stairLinks.size());

        return new Links(doorLinks, stairLinks, undergroundLinks, specialLinks);
    }
}
