package github.dqw4w9wgxcq.pathfinder.graphgeneration.tileflagsgrid;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.MovementFlags;
import lombok.Getter;
import net.runelite.api.CollisionDataFlag;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;

public class TileFlagsGrid {
    private static final int IS_BLOCKING_FLAG = 1;
    private static final int PLANE_1_THING_FLAG = 1 << 1;//2

    private final ObjectManager objectManager;
    @Getter
    int[][][] grid;

    public TileFlagsGrid(RegionLoader regionLoader, ObjectManager objectManager) {
        super();
        this.objectManager = objectManager;
        grid = new int[4][(regionLoader.getHighestX().getRegionX() + 1) * 64][(regionLoader.getHighestY().getRegionY() + 1) * 64];
        var regions = regionLoader.getRegions();
        for (var region : regions) {
            addFloor(region);
        }

        for (var region : regions) {
            addObjectLocations(region);
        }
    }

    private void addFloor(Region region) {
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var z = 0; z < Region.Z; z++) {
            for (var x = 0; x < Region.X; x++) {
                for (var y = 0; y < Region.Y; y++) {
                    var tileSetting = region.getTileSetting(z, x, y);
                    if ((tileSetting & IS_BLOCKING_FLAG) == 1) {
                        addTileFlag(z, baseX + x, baseY + y, MovementFlags.FLOOR);
                    }
                }
            }
        }
    }

    private void addObjectLocations(Region region) {
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();
        for (var location : region.getLocations()) {
            var id = location.getId();
            var definition = objectManager.getObject(id);
            if (definition.getInteractType() == 0) {//objects with deprioritized action in context menu (ground flowers etc.)
                continue;
            }

            var position = location.getPosition();
            var worldX = baseX + position.getX();
            var worldY = baseY + position.getY();
            var flag = definition.getInteractType() == 1 ? CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION : CollisionDataFlag.BLOCK_MOVEMENT_OBJECT;

            throw new UnsupportedOperationException("TODO");//todo xy size > 1, walls, rotation
        }
    }

    private void addTileFlag(int z, int x, int y, int flag) {
        grid[z][x][y] |= (flag & MovementFlags.VISITED);
    }

//todo theres a special case if floor 1 has setting &2==2 but idk where in game it happens
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
