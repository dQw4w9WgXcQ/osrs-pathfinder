package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.ObjectData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.RegionData;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Position;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DoorLinks {
    public static final Set<String> NAMES = Set.of("Door", "Large door", "Gate");
    public static final Set<Position> IGNORE_LOCATIONS = Set.of(
            new Position(3115, 3450, 0),//hill giant hut
            new Position(3143, 3443, 0),//cooks guild
            new Position(3108, 3353, 0), new Position(3109, 3353, 0)//draynor manor entrance
    );
    public static final Set<Integer> IGNORE_IDS = Set.of(

    );

    public static List<DoorLink> find(RegionData regionData, ObjectData objectData) {
        log.info("door links");

        var doorIds = findDoorIds(objectData.definitions().values());
        log.info("found {} doorIds", doorIds.size());

        for (var region : regionData.regions().values()) {
            var doorCount = 0;
            for (var location : region.getLocations()) {
                if (doorIds.containsKey(location.getId())) {
                    doorCount++;
                }
            }

            if (doorCount != 0) {
                log.debug("Found {} doors in region x:{} y:{}", doorCount, region.getRegionX(), region.getRegionY());
            }
        }

        throw new RuntimeException("Not implemented");//todo
    }

    private static Map<Integer, ObjectDefinition> findDoorIds(Collection<ObjectDefinition> allDefinitions) {
        return allDefinitions
                .stream()
                .filter(DoorLinks::isDoor)
                .collect(Collectors.toMap(ObjectDefinition::getId, x -> x));
    }

    private static boolean isDoor(ObjectDefinition definition) {
        var name = definition.getName();
        if (!NAMES.contains(name)) {
            return false;
        }

        if (IGNORE_IDS.contains(definition.getId())) {
            log.debug("Ignoring door id {}", definition.getId());
            return false;
        }

        for (var action : definition.getActions()) {
            if ("Open".equals(action)) {
                log.debug("found a door name:{} id:{}", name, definition.getId());
                return true;
            }
        }

        return false;
    }

    private static Cardinal determineDoorDirection(int type, int orientation) {
        return switch (type) {
            case 0 -> {
                switch (orientation) {
                    case 0 -> {
                        yield Cardinal.W;
                    }
                    case 1 -> {
                        yield Cardinal.N;
                    }
                    case 2 -> {
                        yield Cardinal.E;
                    }
                    case 3 -> {
                        yield Cardinal.S;
                    }
                    default -> throw new RuntimeException("type " + type + " orientation " + orientation);
                }
            }
            default -> {
                throw new RuntimeException("no handler for type " + type);
            }
        };
    }

    private enum Cardinal {
        N,
        E,
        S,
        W,
    }
}
