package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import github.dqw4w9wgxcq.pathfinder.commons.domain.link.WildernessDitchLink;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGrid;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WildernessDitchLinks {
    public static final int DITCH_ID = ObjectID.WILDERNESS_DITCH;
    public static final int DITCH_Y = 3521;
    public static final int DITCH_Y_SOUTH = DITCH_Y - 1;
    public static final int DITCH_Y_NORTH = DITCH_Y + 2;

    public static List<WildernessDitchLink> find(List<Location> objectLocations, ComponentGrid componentGrid) {
        log.info("finding wilderness ditch links");
        var startTime = System.currentTimeMillis();

        var links = new ArrayList<WildernessDitchLink>();

        int id = 0;
        for (Location objectLocation : objectLocations) {
            if (objectLocation.getId() != DITCH_ID) {
                continue;
            }

            if (objectLocation.getPosition().getY() != DITCH_Y) {
                log.debug("black knight's fortress {}", objectLocation.getPosition());
                continue;
            }

            var x = objectLocation.getPosition().getX();
            if (x % 10 != 0) {
                log.debug("skipping {}", objectLocation.getPosition());
                continue;
            }

            var north = new Position(x, DITCH_Y_NORTH, 0);

            if (componentGrid.isBlocked(north)) {
                log.debug("north blocked {}", north);
                continue;
            }

            var south = new Position(x, DITCH_Y_SOUTH, 0);
            if (componentGrid.isBlocked(south)) {
                log.debug("south blocked {}", south);
                continue;
            }

            links.add(new WildernessDitchLink(id++, north, south));
            links.add(new WildernessDitchLink(id++, south, north));
        }

        var time = System.currentTimeMillis() - startTime;
        log.info("found {} wilderness ditch links in {}ms", links.size(), time);
        return links;
    }
}
