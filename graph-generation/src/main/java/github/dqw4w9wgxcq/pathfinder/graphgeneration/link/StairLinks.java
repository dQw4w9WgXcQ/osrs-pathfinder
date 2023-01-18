package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.domain.link.StairLink;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;
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

    public static List<StairLink> find(CacheData cacheData, List<Location> objectLocations) {
        var definitions = cacheData.objectData().definitions();

        var out = new ArrayList<StairLink>();
        for (var location : objectLocations) {
            var id = location.getId();
            var definition = definitions.get(id);

            Preconditions.checkState(definition != null, "definition for id {} shouldn't be null at this point", id);

            if (isUpObject(definition)) {
                log.debug("Found up object: {} at {}", definition.getName(), location.getPosition());
            }

            if (isDownObject(definition)) {
                log.debug("Found down object: {} at {}", definition.getName(), location.getPosition());
            }
        }

        return out;
    }

    public static boolean isUpObject(ObjectDefinition definition) {
        var action = definition.getActions()[0];
        return action != null && action.equals(UP_ACTION);
    }

    public static boolean isDownObject(ObjectDefinition definition) {
        var action = definition.getActions()[0];
        return action != null && action.equals(DOWN_ACTION);
    }
}
