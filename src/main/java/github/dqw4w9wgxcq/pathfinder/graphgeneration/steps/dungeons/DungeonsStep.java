package github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.dungeons;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.steps.GenerationStep;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

@AllArgsConstructor
public class DungeonsStep implements GenerationStep {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    @Override
    public void accept(Graph graph) {
        throw new UnsupportedOperationException("TODO");//todo
    }
}
