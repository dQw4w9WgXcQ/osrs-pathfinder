package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.ObjectData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.RegionData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.RegionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Region;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class GridWorld {
    public static final int PLANES_SIZE = 4;

    @Getter
    private final int sizeX, sizeY;
    @Getter
    private final TileGrid[] planes;

    @VisibleForTesting
    GridWorld(int regionSizeX, int regionSizeY) {
        log.info("Creating GridWorld with regionSizeX:{} Y:{}", regionSizeX, regionSizeY);

        this.sizeX = (regionSizeX + 1) * RegionUtil.SIZE;
        this.sizeY = (regionSizeY + 1) * RegionUtil.SIZE;

        planes = new TileGrid[PLANES_SIZE];
        Arrays.setAll(planes, x -> new TileGrid(sizeX, sizeY));
    }

    public static GridWorld create(RegionData regionData, ObjectData objectData) {
        var out = new GridWorld(regionData.highestRegionX(), regionData.highestRegionY());

        for (var region : regionData.regions().values()) {
            markRegionValid(out.planes, region);
        }

        for (var region : regionData.regions().values()) {
            markFloorFromRegion(out.planes, region);
        }

        for (var region : regionData.regions().values()) {
            markObjectFromRegion(out.planes, region, objectData.definitions());
        }

        return out;
    }

    public TileGrid getPlane(int plane) {
        Preconditions.checkArgument(plane >= 0 && plane < PLANES_SIZE, "plane must be between 0 and 3");

        return planes[plane];
    }

    @VisibleForTesting
    static void markRegionValid(TileGrid[] planes, Region region) {
        for (var plane : planes) {
            plane.markHaveData(region.getRegionX(), region.getRegionY());
        }
    }

    @VisibleForTesting
    static void markFloorFromRegion(TileGrid[] planes, Region region) {
        log.debug("adding floor from region " + region.getRegionID() + " x" + region.getRegionX() + "y" + region.getRegionY());
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var renderPlane = 0; renderPlane < planes.length; renderPlane++) {
            for (var x = 0; x < RegionUtil.SIZE; x++) {
                for (var y = 0; y < RegionUtil.SIZE; y++) {
                    var tileSetting = region.getTileSetting(renderPlane, x, y);
                    if ((tileSetting & 1) == 1) {
                        var collisionPlane = renderPlane;
                        if ((region.getTileSetting(1, x, y) & 2) == 2) {
                            collisionPlane = renderPlane - 1;
                        }

                        if (collisionPlane >= 0) {
                            planes[collisionPlane].markTile(x + baseX, y + baseY, TileFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    @VisibleForTesting
    static void markObjectFromRegion(TileGrid[] planes, Region region, Map<Integer, ObjectDefinition> definitions) {
        log.debug("adding objects to planes");

        for (var location : region.getLocations()) {
            var position = location.getPosition();
            var x = position.getX() - region.getBaseX();
            var y = position.getY() - region.getBaseY();
            var renderPlane = position.getZ();
            var collisionPlane = renderPlane;
            if ((region.getTileSetting(1, x, y) & 2) == 2) {
                collisionPlane = renderPlane - 1;
            }

            if (collisionPlane >= 0) {
                markObject(planes[collisionPlane], location, definitions.get(location.getId()));
            }
        }
    }

    //based off code from decompiled game, see end of file for reference
    @VisibleForTesting
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
            case 9,10, 11 -> {
                if (interactType != 0) {
                    grid.markAreaObject(x, y, sizeX, sizeY, false);
                }
            }
            //floor decoration
            case 22 -> {
                if (interactType == 1) {
                    grid.markAreaObject(x, y, sizeX, sizeY, true);
                }
            }
            default -> {
                //
                if (locationType >= 12 && locationType <= 21) {
                    if (interactType != 0) {
                        grid.markAreaObject(x, y, sizeX, sizeY, false);
                    }
                } else {
                    throw new IllegalArgumentException("expect:  0 =< locationType <= 22, found:" + locationType);
                }
            }
        }
    }
}

/*
//add object to Scene(dont care about) and CollisionMap
static final void method3050(final int z,
                             final int z2,
                             final int x,
                             final int y,
                             final int id,
                             final int orientation,
                             final int locationType,
                             final Scene scene,
                             final CollisionMap collisionMap)
{
    ObjectComposition objectDefinition = class162.getObjectDefinition(id); // L: 956
    final int sizeX;
    final int sizeY;
    if (orientation == 1 || orientation == 3)
    {
        sizeX = objectDefinition.sizeY; // L: 960
        sizeY = objectDefinition.sizeX; // L: 961
    }
    else
    { // L: 959
        sizeX = objectDefinition.sizeX; // L: 964
        sizeY = objectDefinition.sizeY; // L: 965
    }

    final int someX;
    final int someX2;
    if (sizeX + x <= 104)
    { // L: 971
        someX = (sizeX >> 1) + x; // L: 972
        someX2 = x + (sizeX + 1 >> 1); // L: 973
    }
    else
    {
        someX = x; // L: 976
        someX2 = x + 1; // L: 977
    }

    final int someY;
    final int someY2;
    if (y + sizeY <= 104)
    { // L: 979
        someY = y + (sizeY >> 1); // L: 980
        someY2 = y + (sizeY + 1 >> 1); // L: 981
    }
    else
    {
        someY = y; // L: 984
        someY2 = y + 1; // L: 985
    }

    final int[][] tileHeights = Tiles.Tiles_heights[z2]; // L: 987
    final int tileHeight = tileHeights[someX][someY2] + tileHeights[someX2][someY] + tileHeights[someX][someY] + tileHeights[someX2][someY2] >> 2; // L: 988
    final int localX = (x << 7) + (sizeX << 6); // L: 989
    final int localY = (y << 7) + (sizeY << 6); // L: 990
    final long tag = TaskHandler.calculateTag(x, y, 2, objectDefinition.int1 == 0, id); // L: 991
    int objectFlags = (orientation << 6) + locationType; // L: 992
    if (objectDefinition.int3 == 1)
    { // L: 993
        objectFlags += 256;
    }

    if (locationType == 22)
    { // L: 994
        Renderable renderable;
        if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
        { // L: 996
            renderable = objectDefinition.getModel(locationType, orientation, tileHeights, localX, tileHeight, localY);
        }
        else
        {
            renderable = new DynamicObject(id, locationType, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 997
        }

        scene.newFloorDecoration(z, x, y, tileHeight, renderable, tag, objectFlags); // L: 998
        if (objectDefinition.interactType == 1)
        { // L: 999
            collisionMap.setBlockedByFloorDec(x, y);
        }
    }
    else if (locationType >= 12)
    { // L: 1010
        Renderable renderable;

        if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
        { // L: 1012
            renderable = objectDefinition.getModel(locationType, orientation, tileHeights, localX, tileHeight, localY);
        }
        else
        {
            renderable = new DynamicObject(id, locationType, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1013
        }

        scene.newGameObjectWithOffset(z, x, y, tileHeight, 1, 1, renderable, 0, tag, objectFlags); // L: 1014
        if (objectDefinition.interactType != 0)
        { // L: 1015
            collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.blocksLineOfSight);
        }
    }
    else
    {
        final Renderable renderable;
        Renderable renderable2;
        switch (locationType)
        {
            case 0:  // L: 1018
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1020
                    renderable = objectDefinition.getModel(0, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 0, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1021
                }

                scene.newBoundaryObject(z, x, y, tileHeight, renderable, null, Tiles.field1007[orientation], 0, tag, objectFlags); // L: 1022

                if (objectDefinition.interactType != 0)
                { // L: 1023
                    collisionMap.method3878(x, y, locationType, orientation, objectDefinition.blocksLineOfSight);
                }

                break;
            case 1:  // L: 1026
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1028
                    renderable = objectDefinition.getModel(1, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 1, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1029
                }

                scene.newBoundaryObject(z, x, y, tileHeight, renderable, null, Tiles.field1011[orientation], 0, tag, objectFlags); // L: 1030

                if (objectDefinition.interactType != 0)
                { // L: 1031
                    collisionMap.method3878(x, y, locationType, orientation, objectDefinition.blocksLineOfSight);
                }

                break;
            case 2:  // L: 1034
                int xd = orientation + 1 & 3; // L: 1035

                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1038
                    renderable = objectDefinition.getModel(2, orientation + 4, tileHeights, localX, tileHeight, localY); // L: 1039
                    renderable2 = objectDefinition.getModel(2, xd, tileHeights, localX, tileHeight, localY); // L: 1040
                }
                else
                {
                    renderable = new DynamicObject(id, 2, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1043
                    renderable2 = new DynamicObject(id, 2, xd, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1044
                }

                scene.newBoundaryObject(z, x, y, tileHeight, renderable, renderable2, Tiles.field1007[orientation], Tiles.field1007[xd], tag, objectFlags); // L: 1046

                if (objectDefinition.interactType != 0)
                { // L: 1047
                    collisionMap.method3878(x, y, locationType, orientation, objectDefinition.blocksLineOfSight);
                }

                break;
            case 3:  // L: 1050
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1052
                    renderable = objectDefinition.getModel(3, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 3, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1053
                }

                scene.newBoundaryObject(z, x, y, tileHeight, renderable, null, Tiles.field1011[orientation], 0, tag, objectFlags); // L: 1054

                if (objectDefinition.interactType != 0)
                { // L: 1055
                    collisionMap.method3878(x, y, locationType, orientation, objectDefinition.blocksLineOfSight);
                }

                break;
            case 4:  // L: 1066
            {
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1068
                    renderable = objectDefinition.getModel(4, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 4, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1069
                }

                scene.newWallDecoration(z, x, y, tileHeight, renderable, null, Tiles.field1007[orientation], 0, 0, 0, tag, objectFlags); // L: 1070

                break;
            }
            case 5:
            { // L: 1073
                int var23 = 16; // L: 1074

                long var24 = scene.getBoundaryObjectTag(z, x, y); // L: 1075

                if (0L != var24)
                { // L: 1076
                    var23 = class162.getObjectDefinition(Decimator.Entity_unpackID(var24)).int2;
                }
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1078
                    renderable = objectDefinition.getModel(4, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 4, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1079
                }

                scene.newWallDecoration(z, x, y, tileHeight, renderable, null, Tiles.field1007[orientation], 0, var23 * Tiles.field1003[orientation], var23 * Tiles.field1005[orientation], tag, objectFlags); // L: 1080

                break;
            }
            case 6:
            { // L: 1083
                int var23 = 8; // L: 1084

                long var24 = scene.getBoundaryObjectTag(z, x, y); // L: 1085

                if (var24 != 0L)
                { // L: 1086
                    var23 = class162.getObjectDefinition(Decimator.Entity_unpackID(var24)).int2 / 2;
                }

                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1088
                    renderable = objectDefinition.getModel(4, orientation + 4, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 4, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1089
                }

                scene.newWallDecoration(z, x, y, tileHeight, renderable, null, 256, orientation, var23 * Tiles.field1006[orientation], var23 * Tiles.field1002[orientation], tag, objectFlags); // L: 1090

                break;
            }
            case 7:  // L: 1093
            {
                int var29 = orientation + 2 & 3; // L: 1095

                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1096
                    renderable = objectDefinition.getModel(4, var29 + 4, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 4, var29 + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1097
                }

                scene.newWallDecoration(z, x, y, tileHeight, renderable, null, 256, var29, 0, 0, tag, objectFlags); // L: 1098

                break;
            }
            case 8:
            { // L: 1101
                int var23 = 8; // L: 1102

                long var24 = scene.getBoundaryObjectTag(z, x, y); // L: 1103

                if (var24 != 0L)
                { // L: 1104
                    var23 = class162.getObjectDefinition(Decimator.Entity_unpackID(var24)).int2 / 2;
                }

                int var28 = orientation + 2 & 3; // L: 1107

                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1108
                    renderable = objectDefinition.getModel(4, orientation + 4, tileHeights, localX, tileHeight, localY); // L: 1109
                    renderable2 = objectDefinition.getModel(4, var28 + 4, tileHeights, localX, tileHeight, localY); // L: 1110
                }
                else
                {
                    renderable = new DynamicObject(id, 4, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1113
                    renderable2 = new DynamicObject(id, 4, var28 + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1114
                }

                scene.newWallDecoration(z, x, y, tileHeight, renderable, renderable2, 256, orientation, var23 * Tiles.field1006[orientation], var23 * Tiles.field1002[orientation], tag, objectFlags); // L: 1116
                break;
            }
            case 9:  // L: 1058
            {
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1060
                    renderable = objectDefinition.getModel(locationType, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, locationType, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1061
                }

                scene.newGameObjectWithOffset(z, x, y, tileHeight, 1, 1, renderable, 0, tag, objectFlags); // L: 1062

                if (objectDefinition.interactType != 0)
                { // L: 1063
                    collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.blocksLineOfSight);
                }

                break;
            }
            case 10:
            case 11:
            {
                if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
                { // L: 1004
                    renderable = objectDefinition.getModel(10, orientation, tileHeights, localX, tileHeight, localY);
                }
                else
                {
                    renderable = new DynamicObject(id, 10, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1005
                }

                if (renderable != null)
                { // L: 1006
                    scene.newGameObjectWithOffset(z, x, y, tileHeight, sizeX, sizeY, renderable, locationType == 11 ? 256 : 0, tag, objectFlags);
                }

                if (objectDefinition.interactType != 0)
                { // L: 1007
                    collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.blocksLineOfSight);
                }

                break;
            }
        }
    }
}
 */