package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.Links;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;

import java.util.List;

@Slf4j
public class FindLinks {
    public static Links find(
            CacheData cacheData, List<Location> objectLocations, ComponentGrid componentGrid, TileWorld tileWorld) {
        log.info("finding links");
        var startTime = System.currentTimeMillis();

        var doorLinks = DoorLinks.find(cacheData, objectLocations, componentGrid);
        var stairLinks = StairLinks.find(cacheData, objectLocations, componentGrid, tileWorld);
        var dungeonLinks = DungeonLinks.find(cacheData, objectLocations);
        var shipLinks = ShipLinks.find();
        var wildernessDitchLinks = WildernessDitchLinks.find(objectLocations, componentGrid);
        var specialLinks = SpecialLinks.find();
        var teleportLinks = TeleportLinks.find();

        var endTime = System.currentTimeMillis();
        log.info("found links in {}ms", endTime - startTime);

        return new Links(
                doorLinks, stairLinks, dungeonLinks, shipLinks, wildernessDitchLinks, specialLinks, teleportLinks);
    }
}
