package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.domain.link.UndergroundLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import lombok.AllArgsConstructor;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * searches for dungeon entrances in the normal map and links them to the underground area (100 regions aka 6400 tiles up)
 */
@AllArgsConstructor
public class UndergroundLinks {
    public static List<UndergroundLink> find(CacheData cacheData, List<Location> objectLocations) {
        return new ArrayList<>();
    }
}
