package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.ObjectData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.RegionData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
                        log.info("found non-wall door type:{} id:{} at {}", location.getType(), location.getId(), location.getPosition());
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
            log.info("door id:{} but has actions:{}", definition.getId(), actions);
            return false;
        }

        for (var i = 1; i < actions.length; i++) {
            if (actions[i] != null) {
                log.info("door id:{} but has actions:{}", definition.getId(), actions);
                return false;
            }
        }

        return true;
    }

    private static Cardinal determineDoorDirection(int locationType, int orientation) {
        return switch (locationType) {
            case 0 -> switch (orientation) {
                case 0 -> Cardinal.W;
                case 1 -> Cardinal.N;
                case 2 -> Cardinal.E;
                case 3 -> Cardinal.S;
                default ->
                        throw new IllegalArgumentException("cant handle locationType:" + locationType + " orientation:" + orientation);
            };
            default -> throw new IllegalArgumentException("cant handle locationType " + locationType);
        };
    }

    @AllArgsConstructor
    private enum Cardinal {
        N(0, -1),
        E(1, 0),
        S(0, 1),
        W(-1, 0),
        ;

        @Getter
        private final int dx, dy;
    }
}
