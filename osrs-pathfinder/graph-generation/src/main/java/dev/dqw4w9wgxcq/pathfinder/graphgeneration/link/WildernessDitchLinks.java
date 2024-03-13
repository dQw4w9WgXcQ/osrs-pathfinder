package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.WildernessDitchLink;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.cache.region.Location;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedAssignment")
@Slf4j
public class WildernessDitchLinks {
    public static final int DITCH_ID = ObjectID.WILDERNESS_DITCH;
    public static final int DITCH_Y = 3521;
    public static final int DITCH_Y_SOUTH = DITCH_Y - 1;
    public static final int DITCH_Y_NORTH = DITCH_Y + 2;

    public static List<WildernessDitchLink> find(List<Location> objectLocations, ComponentGrid componentGrid) {
        log.info("finding wilderness ditch links");

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

        // add the ditch near black knight's fortress https://i.imgur.com/dXqkXtq.png
        var blackKnightsFortressDitch =
                new WildernessDitchLink(id++, new Position(2995, 3532, 0), new Position(2998, 3532, 0));
        links.add(blackKnightsFortressDitch);
        links.add(new WildernessDitchLink(id++, blackKnightsFortressDitch.end(), blackKnightsFortressDitch.start()));

        return links;
    }
}
