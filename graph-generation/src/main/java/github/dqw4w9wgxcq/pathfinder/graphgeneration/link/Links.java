package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graph.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graph.domain.link.DoorLink;
import github.dqw4w9wgxcq.pathfinder.graph.domain.link.SpecialLink;
import github.dqw4w9wgxcq.pathfinder.graph.domain.link.StairLink;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public record Links(List<DoorLink> doorLinks, List<SpecialLink> specialLinks, List<StairLink> stairLinks) {
    public static Links create(CacheData cacheData, List<Location> objectLocations, ContiguousComponents contiguousComponents) {
        log.info("finding links");

        var doorLinks = DoorLinks.find(cacheData, objectLocations);
        var specialLinks = SpecialLinks.find();
        var stairLinks = StairLinks.find(cacheData, objectLocations);

        for (var links : List.of(doorLinks, specialLinks, stairLinks)) {
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

        return new Links(doorLinks, specialLinks, stairLinks);
    }

    public List<Link> all() {
        var out = new ArrayList<Link>();
        out.addAll(doorLinks);
        out.addAll(specialLinks);
        out.addAll(stairLinks);
        return out;
    }
}
