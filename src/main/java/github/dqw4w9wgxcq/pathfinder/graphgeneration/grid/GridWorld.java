package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.utils.RegionUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Region;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class GridWorld {
    public static final int PLANES_SIZE = 4;
    private final int sizeX;
    private final int sizeY;
    private final TileGrid[] planes;

    public GridWorld(int sizeX, int sizeY) {
        log.info("Creating grid world with size x{}y{}", sizeX, sizeY);

        planes = new TileGrid[Region.Z];
        Arrays.fill(planes, new TileGrid(sizeX, sizeY));

        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    private void addFloorFlags(Region region) {
        log.debug("adding floor region:{} (x:{} y:{})", region.getRegionID(), region.getRegionX(), region.getRegionY());

        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var z = 0; z < planes.length; z++) {
            for (var x = 0; x < RegionUtils.SIZE; x++) {
                for (var y = 0; y < RegionUtils.SIZE; y++) {
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

    private void addObjectLocations(Region region, Map<Integer, ObjectDefinition> definitions) {
        for (var location : region.getLocations()) {
            planes[location.getPosition().getZ()].addObjectLocation(location, definitions);
        }
    }
}
