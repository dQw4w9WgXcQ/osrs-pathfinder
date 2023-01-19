package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.domain.link.StairLink;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * searches for ladders/stairs and links them to ladders/stairs in plane+-1
 */
@AllArgsConstructor
@Slf4j
public class StairLinks {
    public static final String UP_ACTION = "Climb-up";
    public static final String DOWN_ACTION = "Climb-down";
    public static final Set<Integer> IGNORE_IDS = Set.of(

    );
    public static final Set<String> IGNORE_NAMES = Set.of(

    );

    public static List<StairLink> find(CacheData cacheData, List<Location> objectLocations, ComponentGrid grid, TileWorld tileWorld) {
        log.info("finding stair links");

        var definitions = cacheData.objectData().definitions();

        var out = new ArrayList<StairLink>();
        var id = 0;
        for (var location : objectLocations) {
            var objId = location.getId();
            var definition = Objects.requireNonNull(definitions.get(objId));

            boolean up;
            if (isUpObject(definition)) {
                log.debug("Found up object: {} at {}", definition.getName(), location.getPosition());
                up = true;
            } else if (isDownObject(definition)) {
                log.debug("Found down object: {} at {}", definition.getName(), location.getPosition());
                up = false;
            } else {
                continue;
            }

            var plane = location.getPosition().getZ();
            if (up) {
                plane++;
            } else {
                plane--;
            }

            if (plane < 0 || plane >= TileWorld.PLANES_SIZE) {
                log.debug("plane out of bounds: {} at location {}", plane, location);
                continue;
            }

            var origin = Util.findNotBlockedAdjacent(tileWorld, location.getPosition(), definition.getSizeX(), definition.getSizeY());
            if (origin == null) {
                log.debug("no adjacent not blocked for origin: {}", location);
                continue;
            }

            var destination = Util.findNotBlockedAdjacent(
                    tileWorld,
                    new Position(location.getPosition().getX(), location.getPosition().getY(), plane),
                    definition.getSizeX(),
                    definition.getSizeY()
            );
            if (destination == null) {
                log.debug("no adjacent not blocked for destination: {}", location);
                continue;
            }

            if (origin.getZ() != 0) {//todo remove
                continue;
            }

            if (location.getPosition().getX() != 3204) {
                continue;
            }

            out.add(new StairLink(id++, Util.fromRlPosition(origin), Util.fromRlPosition(destination), location.getId(), up));
        }

        log.info("found {} stair links", out.size());
        return out;
    }

    private static boolean isUpObject(ObjectDefinition definition) {
        for (var action : definition.getActions()) {
            if (action != null && action.equals(UP_ACTION)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDownObject(ObjectDefinition definition) {
        for (var action : definition.getActions()) {
            if (action != null && action.equals(DOWN_ACTION)) {
                return true;
            }
        }
        return false;
    }
}
