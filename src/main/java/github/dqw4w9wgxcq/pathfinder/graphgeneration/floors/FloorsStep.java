package github.dqw4w9wgxcq.pathfinder.graphgeneration.floors;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.MovementFlags;
import lombok.AllArgsConstructor;
import net.runelite.cache.region.RegionLoader;

import java.util.function.Consumer;

@AllArgsConstructor
public class FloorsStep {
    private static final int FLAG = 1;
    private static final int PLANE_FLAG = 1 << 1;

    private final RegionLoader regionLoader;

    public void accept(Graph graph) {
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            var baseX = region.getBaseX();
            var baseY = region.getBaseY();

            for (var z = 0; z < 4; z++) {
                for (var x = 0; x < 64; x++) {
                    for (var y = 0; y < 64; y++) {
                        var tileSetting = region.getTileSetting(z, x, y);
                        if ((tileSetting & FLAG) == 1) {
                            graph.addTileFlag(z, baseX + x, baseY + y, MovementFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    //special case if floor 1 has setting &2==2 but idk where in game it happens
/*
	static final void method2905(Scene var0, CollisionMap[] var1) {
		int var2;
		int var3;
		int var4;
		int var5;
		for (var2 = 0; var2 < 4; ++var2) { // L: 543
			for (var3 = 0; var3 < 104; ++var3) { // L: 544
				for (var4 = 0; var4 < 104; ++var4) { // L: 545
					if ((Tiles.Tiles_renderFlags[var2][var3][var4] & 1) == 1) { // L: 546
						var5 = var2; // L: 547
						if ((Tiles.Tiles_renderFlags[1][var3][var4] & 2) == 2) { // L: 548
							var5 = var2 - 1;
						}

						if (var5 >= 0) { // L: 549
							var1[var5].setBlockedByFloor(var3, var4);
						}
					}
				}
			}
		}
 */
}
