package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.ObjectData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.RegionData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * searches for ladders/stairs and links them to ladders/stairs in plane+-1
 */
@AllArgsConstructor
@Slf4j
public class StairLinks {
    public static final String UP_ACTION = "Climb-up";
    public static final String DOWN_ACTION = "Climb-down";

    public static List<StairLink> find(RegionData regionData, ObjectData objectData) {
        var regions = regionData.regions();
        var definitions = objectData.definitions();
        var out = new ArrayList<StairLink>();
        for (var region : regions) {
            for (var location : region.getLocations()) {
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
