package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.doors;

import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.RegionLoader;

import java.util.List;

@AllArgsConstructor
public class Doors {
    public static final String OPEN_ACTION = "Open";
    public static final List<String> NAMES = List.of("Door", "Large door", "Gate");
    public static final List<Position> IGNORE_LOCATIONS = List.of(
            new Position(3115, 3450, 0),//hill giant hut
            new Position(3143, 3443, 0),//cooks guild
            new Position(3108, 3353, 0), new Position(3109, 3353, 0)//draynor manor entrance
    );

    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public void findEdges() {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();


        }
    }
}
