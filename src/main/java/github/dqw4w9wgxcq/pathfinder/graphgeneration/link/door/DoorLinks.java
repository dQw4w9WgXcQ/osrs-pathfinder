package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.RegionLoader;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DoorLinks {
    public static final String OPEN_ACTION = "Open";
    public static final List<String> NAMES = List.of("Door", "Large door", "Gate");
    public static final List<Position> IGNORE_LOCATIONS = List.of(
            new Position(3115, 3450, 0),//hill giant hut
            new Position(3143, 3443, 0),//cooks guild
            new Position(3108, 3353, 0), new Position(3109, 3353, 0)//draynor manor entrance
    );

    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public List<Link> findEdges() {
        var regions = regionLoader.getRegions();
        var out = new ArrayList<Link>();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();

            throw new RuntimeException("Not implemented");//todo
        }

        return out;
    }
}