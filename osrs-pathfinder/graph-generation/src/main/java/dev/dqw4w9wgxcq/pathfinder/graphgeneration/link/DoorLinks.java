package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.DoorLink;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.WallDirection;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DoorLinks {
    public static final Set<String> NAMES = Set.of("Door", "Large door", "Gate");
    public static final Set<Integer> IGNORE_IDS = Set.of(
            ObjectID.DOOR_1804 // hill giant
            );
    public static final Set<Integer> ALLOW_IDS = Set.of();

    public static List<DoorLink> find(
            CacheData cacheData, List<Location> objectLocations, ComponentGrid componentGrid) {
        log.info("finding door links");
        var startTime = System.currentTimeMillis();

        var doorIds = findDoorIds(cacheData.objectData().definitions().values());
        log.info("found {} doorIds", doorIds.size());

        List<DoorLink> links = new ArrayList<>();
        var id = 0;
        for (var location : objectLocations) {
            if (!doorIds.contains(location.getId())) {
                continue;
            }

            switch (location.getType()) {
                case 0, 1, 2, 3 -> log.debug("found door at {}", location.getPosition());
                default -> {
                    log.debug(
                            "found non-wall door type:{} id:{} at {}",
                            location.getType(),
                            location.getId(),
                            location.getPosition());
                    continue;
                }
            }

            var direction = determineDoorDirection(location.getType(), location.getOrientation());
            var start = Util.fromRlPosition(location.getPosition());
            var end = new Position(start.x() + direction.getDx(), start.y() + direction.getDy(), start.plane());

            if (componentGrid.componentOf(start) == -1 || componentGrid.componentOf(end) == -1) {
                continue;
            }

            if (componentGrid.componentOf(start) < 0 || componentGrid.componentOf(end) < 0) {
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
                System.out.println(componentGrid.componentOf(start));
                System.out.println(componentGrid.componentOf(end));
                System.out.println(start);
                System.out.println(end);
                System.exit(42069);
            }

            var link = new DoorLink(id++, start, end, location.getId());
            log.debug("new door link {}", link);
            links.add(link);
            // bidirectionality
            links.add(new DoorLink(id++, end, start, location.getId()));
        }

        var endTime = System.currentTimeMillis();
        log.info("found {} door links in {}ms", links.size(), endTime - startTime);
        return links;
    }

    private static Set<Integer> findDoorIds(Collection<ObjectDefinition> definitions) {
        return definitions.stream()
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

    private static WallDirection determineDoorDirection(int locationType, int orientation) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (locationType) {
            case 0 -> switch (orientation) {
                case 0 -> WallDirection.W;
                case 1 -> WallDirection.N;
                case 2 -> WallDirection.E;
                case 3 -> WallDirection.S;
                default -> throw new IllegalArgumentException(
                        "cant handle locationType:" + locationType + " orientation:" + orientation);
            };
            default -> throw new IllegalArgumentException("cant handle locationType " + locationType);
        };
    }
}