package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.StairLink;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;

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
            ObjectID.LADDER_16450,
            ObjectID.LADDER_16556, // goblin village ladder goes from plane 0 to 2
            ObjectID.WOODEN_BEAMS, // ardy rooftop
            ObjectID.CRATE_11632 // draynor rooftop
            );

    public static List<StairLink> find(
            CacheData cacheData, List<Location> objectLocations, ComponentGrid grid, TileWorld tileWorld) {
        log.info("finding stair links");

        var definitions = cacheData.objectData().definitions();

        var out = new ArrayList<StairLink>();
        var id = 0;
        for (var location : objectLocations) {
            //            if (location.getPosition().getZ() != 0 || location.getPosition().getX() != 3204 ||
            // location.getPosition().getY() != 3207) {//todo temp
            //                continue;
            //            }

            var definition = Objects.requireNonNull(definitions.get(location.getId()));

            boolean up;
            if (isUpObject(definition)) {
                up = true;
            } else if (isDownObject(definition)) {
                up = false;
            } else {
                continue;
            }

            if (!isStair(location, definition)) {
                continue;
            }

            log.debug("found stair at {} up:{}", location.getPosition(), up);

            var endPlane = location.getPosition().getZ();
            if (up) {
                endPlane++;
            } else {
                endPlane--;
            }

            if (endPlane < 0 || endPlane >= Constants.PLANES_SIZE) {
                log.debug("stair at {} is out of bounds up:{}", location.getPosition(), up);
                continue;
            }

            int orientation = location.getOrientation();

            int sizeX;
            int sizeY;
            if (orientation == 1 || orientation == 3) {
                sizeX = definition.getSizeY();
                sizeY = definition.getSizeX();
            } else {
                sizeX = definition.getSizeX();
                sizeY = definition.getSizeY();
            }

            Position start = null;
            Position end = null;
            for (var adjacent :
                    Util.findNotBlockedAdjacent(tileWorld, Util.fromRlPosition(location.getPosition()), sizeX, sizeY)) {
                var testEnd = new Position(adjacent.x(), adjacent.y(), endPlane);
                if (grid.componentOf(testEnd) != -1) {
                    start = adjacent;
                    end = testEnd;
                    break;
                }
            }

            if (start == null) {
                log.debug("no adjacent for location:{}", location);
                continue;
            }

            var stairLink = new StairLink(id++, start, end, location.getId(), up);
            log.debug("found stair link: {}", stairLink);
            out.add(stairLink);
        }

        return out;
    }

    private static boolean isStair(Location location, ObjectDefinition definition) {
        int id = location.getId();
        int locationType = location.getType();
        switch (locationType) {
                // wall objects
            case 0, 1, 2, 3 -> {
                log.debug("ignoring wall object {} {} at {}", definition.getName(), id, location);
                return false;
            }
                // wall decoration
            case 4, 5, 6, 7, 8 -> {
                log.debug("ignoring wall decoration {} {} at {}", definition.getName(), id, location);
                return false;
            }
                // game object
            case 9,
                    10,
                    11,
                    // floor decoration
                    22 -> {}
            default -> {
                if (locationType >= 12 && locationType <= 21) {
                    // never happens for now
                    log.info(
                            "ignoring unknown location type({}) {} {} at {}",
                            locationType,
                            definition.getName(),
                            id,
                            location);
                    return false;
                } else {
                    throw new IllegalArgumentException("expect:  0 =< locationType <= 22, found:" + locationType);
                }
            }
        }

        return !IGNORE_IDS.contains(id);
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
