package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.Region;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class DoorLinks {
    public static final String OPEN_ACTION = "Open";
    public static final List<String> NAMES = List.of("Door", "Large door", "Gate");
    public static final List<Position> IGNORE_LOCATIONS = List.of(
            new Position(3115, 3450, 0),//hill giant hut
            new Position(3143, 3443, 0),//cooks guild
            new Position(3108, 3353, 0), new Position(3109, 3353, 0)//draynor manor entrance
    );

    public static List<Link> find(Map<Integer, ObjectDefinition> definitions, Map<Integer, Region> regions) {
        var doorIds = findDoorIds(definitions.values());

        log.debug(doorIds.size() + " doors found");


        throw new RuntimeException("Not implemented");//todo
    }

    static List<Integer> findDoorIds(Collection<ObjectDefinition> definitions) {
        return definitions
                .parallelStream()
                .filter(DoorLinks::isDoor)
                .map(ObjectDefinition::getId)
                .toList();
    }

    static boolean isDoor(ObjectDefinition def) {
        var name = def.getName();
        if (!NAMES.contains(name)) {
            return false;
        }

        log.debug("found a door {} id:{}", name, def.getId());
        return true;
    }
}
