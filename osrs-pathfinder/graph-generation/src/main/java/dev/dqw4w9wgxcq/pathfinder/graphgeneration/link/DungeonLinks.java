package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.DungeonLink;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import net.runelite.api.ObjectID;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * searches for dungeon entrances in the normal map and links them to the underground area (100 regions aka 6400 tiles up)
 */
// todo
@SuppressWarnings("UnusedAssignment")
public class DungeonLinks {
    public static List<DungeonLink> find(CacheData cacheData, List<Location> objectLocations) {
        var links = new ArrayList<DungeonLink>();

        int id = 0;
        var tempMortyniaIn = new DungeonLink(
                id++, new Position(3405, 3506, 0), new Position(3405, 9906, 0), ObjectID.TRAPDOOR_1581, "Climb-down");
        links.add(tempMortyniaIn);
        links.add(
                new DungeonLink(id++, tempMortyniaIn.end(), tempMortyniaIn.start(), ObjectID.LADDER_17385, "Climb-up"));

        var tempMortyniaOut = new DungeonLink(
                id++, new Position(3423, 3485, 0), new Position(3440, 9887, 0), ObjectID.TRAPDOOR_3433, "Climb-down");
        links.add(tempMortyniaOut);

        return links;
    }
}
