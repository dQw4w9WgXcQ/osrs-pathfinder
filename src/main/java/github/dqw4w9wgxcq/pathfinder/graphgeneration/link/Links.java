package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door.DoorLinks;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair.StairLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair.StairLinks;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

public record Links(List<DoorLink> doorLinks, List<StairLink> stairLinks) {
    public static Links create(CacheData cacheData, List<Location> objectLocations) {
        var doorLinks = DoorLinks.find(cacheData, objectLocations);
        var stairLinks = StairLinks.find(cacheData, objectLocations);

        return new Links(doorLinks, stairLinks);
    }

    public List<Link> all() {
        var out = new ArrayList<Link>();
        out.addAll(doorLinks);
        out.addAll(stairLinks);
        return out;
    }
}
