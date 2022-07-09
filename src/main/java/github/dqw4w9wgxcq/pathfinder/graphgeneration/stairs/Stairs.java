package github.dqw4w9wgxcq.pathfinder.graphgeneration.stairs;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

@AllArgsConstructor
public class Stairs {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public void accept(Graph graph) {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();


        }
    }
}
