package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.util.RegionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Region;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class GridWorld {
    public static final int PLANES_SIZE = 4;

    @Getter
    private final int sizeX, sizeY;
    private final TileGrid[] planes;

    GridWorld(int sizeX, int sizeY) {
        log.info("Creating map world with size x{}y{}", sizeX, sizeY);

        planes = new TileGrid[PLANES_SIZE];
        Arrays.setAll(planes, x -> new TileGrid(sizeX, sizeY));

        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public static GridWorld create(CacheData data) {
        var out = new GridWorld(data.highestWorldX(), data.highestWorldY());

        for (var region : data.regions().values()) {
            applyFloorFlagsFromRegion(out.planes, region);
        }

        for (var region : data.regions().values()) {
            applyObjectLocationFlagsFromRegion(out.planes, region.getLocations(), data.objectDefinitions());
        }

        return out;
    }

    public TileGrid getPlane(int plane) {
        Preconditions.checkArgument(plane >= 0 && plane < PLANES_SIZE, "plane must be between 0 and 3");

        return planes[plane];
    }

    static void applyFloorFlagsFromRegion(TileGrid[] planes, Region region) {
        log.debug("adding region " + region.getRegionID() + " x" + region.getRegionX() + "y" + region.getRegionY());
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var z = 0; z < planes.length; z++) {
            for (var x = 0; x < RegionUtil.SIZE; x++) {
                for (var y = 0; y < RegionUtil.SIZE; y++) {
                    var tileSetting = region.getTileSetting(z, x, y);
                    if ((tileSetting & 1) == 1) {
                        var modifiedZ = z;
                        if ((region.getTileSetting(1, x, y) & 2) == 2) {
                            modifiedZ = z - 1;
                            log.trace("z was modified from " + z + " to " + modifiedZ + " at " + "x" + x + "y" + y);
                        }

                        if (modifiedZ >= 0) {
                            planes[modifiedZ].addFlag(x + baseX, y + baseY, TileFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    static void applyObjectLocationFlagsFromRegion(TileGrid[] planes, List<Location> locations, Map<Integer, ObjectDefinition> definitions) {
        for (var location : locations) {
            planes[location.getPosition().getZ()].addObjectLocation(location, definitions);
        }
    }
}

//from decompile for reference
/*
//floor flags added when render flags update
private void method2905(int[][][] renderFlags) {
    int var2;
    int var3;
    int var4;
    int var5;
    for (var2 = 0; var2 < 4; ++var2) {
        for (var3 = 0; var3 < 104; ++var3) {
            for (var4 = 0; var4 < 104; ++var4) {
                if ((renderFlags[var2][var3][var4] & 1) == 1) {
                    var5 = var2;
                    if ((renderFlags[1][var3][var4] & 2) == 2) {
                        var5 = var2 - 1;
                    }

                    if (var5 >= 0) {
                        addTileFlag(var2, var3, var4, 2097152);
                    }
                }
            }
        }
    }

    ...
}
 */