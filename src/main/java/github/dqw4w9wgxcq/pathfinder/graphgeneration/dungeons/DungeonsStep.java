package github.dqw4w9wgxcq.pathfinder.graphgeneration.dungeons;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

import java.util.function.Consumer;

@AllArgsConstructor
public class DungeonsStep{
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public void accept(Graph graph) {
        throw new UnsupportedOperationException("TODO");//todo
    }
}
