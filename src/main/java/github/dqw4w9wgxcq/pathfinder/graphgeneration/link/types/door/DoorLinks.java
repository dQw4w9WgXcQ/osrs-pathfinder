package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.ObjectData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.RegionData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.Wall;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Position;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DoorLinks {
    public static final Set<String> NAMES = Set.of("Door", "Large door", "Gate");
    public static final Set<Integer> IGNORE_IDS = Set.of(

    );
    public static final Set<Integer> ALLOW_IDS = Set.of(

    );

    public static Map<Position, DoorLink> find(RegionData regionData, ObjectData objectData) {
        log.info("door links");

        var doorIds = findDoorIds(objectData.definitions().values());
        log.info("found {} doorIds", doorIds.size());

        Map<Position, DoorLink> links = new HashMap<>();
        for (var region : regionData.regions().values()) {
            var doorCount = 0;
            for (var location : region.getLocations()) {
                if (!doorIds.contains(location.getId())) {
                    continue;
                }

                switch (location.getType()) {
                    case 0, 1, 2, 3 -> {
                        log.debug("found door at {}", location.getPosition());
                        doorCount++;
                    }
                    default -> {
                        log.debug("found non-wall door type:{} id:{} at {}", location.getType(), location.getId(), location.getPosition());
                        continue;
                    }
                }

                var direction = determineDoorDirection(location.getType(), location.getOrientation());
                var position = location.getPosition();
                var destination = new Position(
                        position.getX() + direction.getDx(),
                        position.getY() + direction.getDy(),
                        position.getZ()
                );

                var link = new DoorLink(destination, location.getId());
                log.debug("new door link {}", link);
                links.put(position, link);
                doorCount++;
            }

            if (doorCount != 0) {
                log.debug("Found {} doors in region x:{} y:{}", doorCount, region.getRegionX(), region.getRegionY());
            }
        }

        return links;
    }

    /**
     * Some doors can be within the same component, just remove wall flags from world in that case
     *
     * @return remaining doors that actually link between two different components
     */
    public static Map<Position, DoorLink> removeInterComponentDoorsFromWorld(Map<Position, DoorLink> doorLinks, List<ContiguousComponents> componentsPlanes, GridWorld world) {
        log.info("removing inter-component doors, {} links", doorLinks.size());

        var remainingDoors = new HashMap<Position, DoorLink>();
        for (var entry : doorLinks.entrySet()) {
            var position = entry.getKey();
            var doorLink = entry.getValue();

            var x = position.getX();
            var y = position.getY();
            var destX = doorLink.destination().getX();
            var destY = doorLink.destination().getY();

            var idMap = componentsPlanes.get(position.getZ()).idMap();
            var component = idMap[x][y];
            var destComponent = idMap[destX][destY];

            if (component == destComponent) {
                log.info("intercomponent door {},{} to {},{} component:{} to:{}, in plane:{}", x, y, x, y, component, destComponent, position.getZ());
                var wall = Wall.fromDXY(destX - x, destY - y);
                world.getPlane(position.getZ()).unmarkWall(x, y, wall);
            } else {
                remainingDoors.put(position, doorLink);
            }
        }

        log.info("remaining doors {}/{}", remainingDoors.size(), doorLinks.size());
        return remainingDoors;
    }

    private static Set<Integer> findDoorIds(Collection<ObjectDefinition> allDefinitions) {
        return allDefinitions
                .stream()
                .filter(DoorLinks::isDoor)
                .map(ObjectDefinition::getId)
                .collect(Collectors.toSet());
    }

    private static boolean isDoor(ObjectDefinition definition) {
        if (!NAMES.contains(definition.getName())) {
            return false;
        }

        if (IGNORE_IDS.contains(definition.getId())) {
            log.debug("Ignoring door id {}", definition.getId());
            return false;
        }

        if (ALLOW_IDS.contains(definition.getId())) {
            log.debug("Allowing door id {}", definition.getId());
            return true;
        }

        var actions = definition.getActions();
        if (!"Open".equals(actions[0])) {
            log.debug("door id:{} but has actions:{}", definition.getId(), actions);
            return false;
        }

        for (var i = 1; i < actions.length; i++) {
            if (actions[i] != null) {
                log.debug("door id:{} but has actions:{}", definition.getId(), actions);
                return false;
            }
        }

        return true;
    }

    private static Wall determineDoorDirection(int locationType, int orientation) {
        return switch (locationType) {
            case 0 -> switch (orientation) {
                case 0 -> Wall.W;
                case 1 -> Wall.N;
                case 2 -> Wall.E;
                case 3 -> Wall.S;
                default ->
                        throw new IllegalArgumentException("cant handle locationType:" + locationType + " orientation:" + orientation);
            };
            default -> throw new IllegalArgumentException("cant handle locationType " + locationType);
        };
    }
}
