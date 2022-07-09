package github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.floors;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.GenerationStep;
import lombok.AllArgsConstructor;
import net.runelite.cache.region.RegionLoader;

@AllArgsConstructor
public class FloorsStep implements GenerationStep {
    private final RegionLoader regionLoader;

    @Override
    public void accept(Graph graph) {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();

            for (var z = 0; z < 3; z++) {
                for (var x = 0; x < 64; x++) {
                    for (var y = 0; y < 64; y++) {
                        var tileSetting = region.getTileSetting(z, x, y);
                        throw new UnsupportedOperationException("TODO");//todo
                    }
                }
            }
        }
    }
}
