package github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.stairs;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.GenerationStep;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

@AllArgsConstructor
public class StairsStep implements GenerationStep {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    @Override
    public void accept(Graph graph) {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();


        }
    }
}
