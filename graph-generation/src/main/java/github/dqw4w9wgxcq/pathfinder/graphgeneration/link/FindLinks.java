package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Links;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;

import java.util.List;

@Slf4j
public class FindLinks {
    public static Links find(CacheData cacheData, List<Location> objectLocations, ComponentGrid componentGrid, TileWorld tileWorld) {
        log.info("finding links");

        var doorLinks = DoorLinks.find(cacheData, objectLocations, componentGrid);
        var stairLinks = StairLinks.find(cacheData, objectLocations, componentGrid, tileWorld);
        var dungeonLinks = DungeonLinks.find(cacheData, objectLocations);
        var specialLinks = SpecialLinks.find();

        log.info("found {} links", doorLinks.size() + specialLinks.size() + stairLinks.size());

        return new Links(doorLinks, stairLinks, dungeonLinks, specialLinks);
    }
}
