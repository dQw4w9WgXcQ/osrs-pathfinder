package github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import github.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingGrid;
import github.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingWorld;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.TileFlags;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Region;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class TileWorld {
    public static final int PLANES_SIZE = 4;

    @Getter
    private final int sizeX, sizeY;
    @Getter
    private final TileGrid[] planes;

    TileWorld(int regionSizeX, int regionSizeY) {
        this.sizeX = (regionSizeX + 1) * Util.REGION_SIZE;
        this.sizeY = (regionSizeY + 1) * Util.REGION_SIZE;

        planes = new TileGrid[PLANES_SIZE];
        Arrays.setAll(planes, x -> new TileGrid(sizeX, sizeY));
    }

    public static TileWorld create(CacheData cacheData, List<Location> objectLocations) {
        var regionData = cacheData.regionData();

        var regionSizeX = regionData.highestRegionX();
        var regionSizeY = regionData.highestRegionY();

        log.info("Creating TileWorld with regionSizeX:{} Y:{}", regionSizeX, regionSizeY);

        var start = System.currentTimeMillis();

        var tileWorld = new TileWorld(regionSizeX, regionSizeY);

        for (var region : regionData.regions()) {
            markRegionValid(tileWorld.planes, region);
        }

        for (var region : regionData.regions()) {
            markFloorForRegion(tileWorld.planes, region);
        }

        markObjectLocations(tileWorld.planes, objectLocations, cacheData.objectData().definitions());

        var time = (System.currentTimeMillis() - start) / 1000;
        log.info("TileWorld created in {}s", time);

        return tileWorld;
    }

    public PathfindingWorld toPathfindingWorld() {
        log.info("Converting to PathfindingWorld");
        var start = System.currentTimeMillis();
        var planes = Arrays.stream(this.planes)
                .parallel()
                .map(TileGrid::toPathfindingGrid)
                .toArray(PathfindingGrid[]::new);
        var time = (System.currentTimeMillis() - start) / 1000;
        log.info("Converted to PathfindingWorld in {}s", time);
        return new PathfindingWorld(planes);
    }

    static void markRegionValid(TileGrid[] planes, Region region) {
        for (var plane : planes) {
            plane.markRegionHaveData(region.getRegionX(), region.getRegionY());
        }
    }

    static void markFloorForRegion(TileGrid[] planes, Region region) {
        log.debug("adding floor from region " + region.getRegionID() + " x" + region.getRegionX() + "y" + region.getRegionY());
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var renderPlane = 0; renderPlane < planes.length; renderPlane++) {
            for (var x = 0; x < Util.REGION_SIZE; x++) {
                for (var y = 0; y < Util.REGION_SIZE; y++) {
                    if ((region.getTileSetting(renderPlane, x, y) & 0x1) == 0x1) {
                        var collisionPlane = renderPlane;
                        if ((region.getTileSetting(1, x, y) & 0x2) == 0x2) {
                            collisionPlane--;
                        }

                        if (collisionPlane >= 0) {
                            planes[collisionPlane].markTile(x + baseX, y + baseY, TileFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    static void markObjectLocations(TileGrid[] planes, List<Location> locations, Map<Integer, ObjectDefinition> definitions) {
        log.debug("adding objects to planes");

        for (var location : locations) {
            markObject(planes[location.getPosition().getZ()], location, definitions.get(location.getId()));
        }
    }

    //based off code from decompiled game client
    static void markObject(TileGrid grid, Location location, ObjectDefinition definition) {
        log.debug("adding object " + location);

        var position = location.getPosition();

        var x = position.getX();
        var y = position.getY();

        log.debug("obj location name : " + definition.getName());

        var orientation = location.getOrientation();

        //rotate according to the orientation
        int sizeX;
        int sizeY;
        if (orientation == 1 || orientation == 3) {
            sizeX = definition.getSizeY();
            sizeY = definition.getSizeX();
        } else {
            sizeX = definition.getSizeX();
            sizeY = definition.getSizeY();
        }

        var locationType = location.getType();
        var interactType = definition.getInteractType();
        switch (locationType) {
            //wall objects
            case 0, 1, 2, 3 -> {
                if (interactType != 0) {
                    grid.markWallObject(x, y, locationType, orientation);
                }
            }
            //wall decoration
            case 4, 5, 6, 7, 8 -> {
                //no collisions
            }
            //game object
            case 9, 10, 11 -> {
                if (interactType != 0) {
                    grid.markObject(x, y, sizeX, sizeY, false);
                }
            }
            //floor decoration
            case 22 -> {
                if (interactType == 1) {
                    grid.markObject(x, y, sizeX, sizeY, true);
                }
            }
            default -> {
                if (locationType >= 12 && locationType <= 21) {
                    if (interactType != 0) {
                        grid.markObject(x, y, sizeX, sizeY, false);
                    }
                } else {
                    throw new IllegalArgumentException("expect:  0 =< locationType <= 22, found:" + locationType);
                }
            }
        }
    }
}