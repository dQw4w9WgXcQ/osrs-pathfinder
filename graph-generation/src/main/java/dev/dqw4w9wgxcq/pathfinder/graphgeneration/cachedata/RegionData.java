package dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.fs.Store;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;
import net.runelite.cache.util.XteaKeyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// todo need to handle runelite returns empty locations list if no xteas for region
@Slf4j
public record RegionData(Collection<Region> regions, int highestRegionX, int highestRegionY) {
    static RegionData load(Store store, XteaKeyManager xteaKeyManager)
            throws IOException, JsonIOException, JsonSyntaxException {
        var regionLoader = new RegionLoader(store, xteaKeyManager);

        regionLoader.loadRegions(); // throws JsonIOException, JsonSyntaxException

        var regions = regionLoader.getRegions();

        regionLoader.calculateBounds();
        var highestRegionX = regionLoader.getHighestX().getRegionX();
        var highestRegionY = regionLoader.getHighestY().getRegionY();

        log.info(
                "loaded {} regions, highestRegionX:{} highestRegionY:{}",
                regions.size(),
                highestRegionX,
                highestRegionY);

        return new RegionData(regions, highestRegionX, highestRegionY);
    }

    /**
     * The 0x2 render flag signifies that objects from the plane above should affect the collision map of the plane below.  Used for bridges and multi-level buildings.
     */
    public List<Location> getLocationsAdjustedFor0x2() {
        log.info("getting 0x2 adjusted locations");
        var locations = new ArrayList<Location>();
        for (var region : regions) {
            for (var location : region.getLocations()) {
                var position = location.getPosition();
                var z = position.getZ();
                var tileSetting = region.getTileSetting(
                        1, position.getX() - region.getBaseX(), position.getY() - region.getBaseY());
                if ((tileSetting & 0x2) == 0x2) {
                    log.debug("location is 0x2: {}", location);
                    z--;
                }

                var adjLocation = new Location(
                        location.getId(),
                        location.getType(),
                        location.getOrientation(),
                        new Position(position.getX(), position.getY(), z));

                if (z < 0) {
                    log.debug("0x2 location is below 0: {}", location);
                    continue;
                }

                locations.add(adjLocation);
            }
        }

        log.info("found {} locations after 0x2", locations.size());
        return locations;
    }
}