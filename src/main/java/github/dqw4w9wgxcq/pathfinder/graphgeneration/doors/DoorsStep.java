package github.dqw4w9wgxcq.pathfinder.graphgeneration.doors;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

import java.util.List;

@AllArgsConstructor
public class DoorsStep {
    public static final String OPEN_ACTION = "Open";
    public static final List<String> NAMES = List.of("Door", "Large door", "Gate");
    public static final List<WorldPoint> IGNORE_LOCATIONS = List.of(
            new WorldPoint(3115, 3450, 0),//hill giant hut
            new WorldPoint(3143, 3443, 0),//cooks guild
            new WorldPoint(3108, 3353, 0), new WorldPoint(3109, 3353, 0)//draynor manor entrance
    );

    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public void accept(Graph graph) {
        throw new UnsupportedOperationException("TODO");//todo
    }
}
