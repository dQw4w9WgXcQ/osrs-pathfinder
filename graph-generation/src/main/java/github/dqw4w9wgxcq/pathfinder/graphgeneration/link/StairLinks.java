package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.StairLink;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    );
    public static final Set<String> IGNORE_NAMES = Set.of(

    );

    public static List<StairLink> find(CacheData cacheData, List<Location> objectLocations, ComponentGrid grid, TileWorld tileWorld) {
        log.info("finding stair links");
        var startTime = System.currentTimeMillis();

        var definitions = cacheData.objectData().definitions();

        var out = new ArrayList<StairLink>();
        var id = 0;
        for (var location : objectLocations) {
//            if (location.getPosition().getZ() != 0 || location.getPosition().getX() != 3204 || location.getPosition().getY() != 3207) {//todo temp
//                continue;
//            }

            var objId = location.getId();
            var definition = Objects.requireNonNull(definitions.get(objId));

            boolean up;
            if (isUpObject(definition)) {
                up = true;
            } else if (isDownObject(definition)) {
                up = false;
            } else {
                continue;
            }

            log.debug("found stair at {} up:{}", location.getPosition(), up);

            var destinationPlane = location.getPosition().getZ();
            if (up) {
                destinationPlane++;
            } else {
                destinationPlane--;
            }

            if (destinationPlane < 0 || destinationPlane >= TileWorld.PLANES_SIZE) {
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

            Position origin = null;
            Position destination = null;
            for (var adjacent : Util.findNotBlockedAdjacent(tileWorld, Util.fromRlPosition(location.getPosition()), sizeX, sizeY)) {
                var testDestination = new Position(adjacent.x(), adjacent.y(), destinationPlane);
                if (grid.componentOf(testDestination) != -1) {
                    origin = adjacent;
                    destination = testDestination;
                    break;
                }
            }

            if (origin == null) {
                log.debug("no adjacent for location:{}", location);
                continue;
            }

            var stairLink = new StairLink(id++, origin, destination, location.getId(), up);
            log.debug("found stair link: {}", stairLink);
            out.add(stairLink);
        }

        var endTime = System.currentTimeMillis();
        log.info("found {} stair links in {}ms", out.size(), endTime - startTime);
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
