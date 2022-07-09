package github.dqw4w9wgxcq.pathfinder.graphgeneration.objectspawns;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import lombok.AllArgsConstructor;
import net.runelite.api.CollisionDataFlag;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

import java.util.function.Consumer;

@AllArgsConstructor
public class ObjectSpawnsStep {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;


    public void accept(Graph graph) {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();
            for (var location : region.getLocations()) {
                var id = location.getId();
                var definition = objectManager.getObject(id);
                if (definition.getInteractType() == 0) {
                    continue;
                }

                var position = location.getPosition();
                var worldX = baseX + position.getX();
                var worldY = baseY + position.getY();
                var flag = definition.getInteractType() == 1 ? CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION : CollisionDataFlag.BLOCK_MOVEMENT_OBJECT;
                graph.addTileFlag(position.getZ(), worldX, worldY, flag);

                throw new UnsupportedOperationException("TODO");//todo xy size > 1, walls
            }
        }
    }
}
