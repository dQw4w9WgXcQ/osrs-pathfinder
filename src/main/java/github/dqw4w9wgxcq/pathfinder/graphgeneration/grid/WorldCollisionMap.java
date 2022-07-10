package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;

import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.LocationsDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;

/**
 * this class is modeled after the collision map from the decompiled game client, but is global instead of scene
 *
 * @see net.runelite.api.CollisionData
 */
@Slf4j
public class WorldCollisionMap {
    private final ObjectManager objectManager;
    private final int[][][] flags;

    public WorldCollisionMap(RegionLoader regionLoader, ObjectManager objectManager) {
        this.objectManager = objectManager;
        var xSize = (regionLoader.getHighestX().getRegionX() + 1) * Region.X;
        var ySize = (regionLoader.getHighestY().getRegionY() + 1) * Region.Y;

        flags = new int[Region.Z][xSize][ySize];

        var regions = regionLoader.getRegions();

        for (var region : regions) {
            addFloorFromRegion(region);
        }

        for (var region : regions) {
            addLocationsFromRegion(region);
        }
    }

    private void addFloorFromRegion(Region region) {
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var z = 0; z < Region.Z; z++) {
            for (var regionX = 0; regionX < Region.X; regionX++) {
                for (var regionY = 0; regionY < Region.Y; regionY++) {
                    var tileSetting = region.getTileSetting(z, regionX, regionY);
                    if ((tileSetting & 1) == 1) {
                        //not sure where this happens in game, but this behavior is from decompiled client
                        //just gonna ignore it 4 now
                        //todo
                        var modifiedZ = z;
//                        if ((region.getTileSetting(1, regionX, regionY) & 2) == 2) {
//                            modifiedZ = z - 1;
//                        }

                        if (modifiedZ >= 0) {
                            addFlag(modifiedZ, regionX + baseX, regionY + baseY, TileFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    /**
     * adds flags for all locations in the region
     *
     * @param region must already be loaded
     * @see Region#loadLocations(LocationsDefinition)
     */
    public void addLocationsFromRegion(Region region) {
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();
        for (var location : region.getLocations()) {
            addLocation(location, baseX, baseY);
        }
    }

    private void addLocation(Location location, int baseX, int baseY) {
        var id = location.getId();
        var definition = objectManager.getObject(id);

        var position = location.getPosition();

        var z = position.getZ();
        var x = position.getX() + baseX;
        var y = position.getY() + baseY;

        var orientation = location.getOrientation();

        //rotate according to the orientation
        int sizeX;
        int sizeY;
        if (orientation == 1 || orientation == 3) {
            sizeX = definition.getSizeY(); // L: 960
            sizeY = definition.getSizeX(); // L: 961
        } else { // L: 959
            sizeX = definition.getSizeX(); // L: 964
            sizeY = definition.getSizeY(); // L: 965
        }

        var locationType = location.getType();
        var interactType = definition.getInteractType();
        switch (locationType) {
            //wall objects
            case 0, 1, 2, 3 -> {
                if (interactType != 0) {
                    addWallObject(z, x, y, locationType, orientation);
                }
            }
            //wall decoration
            case 4, 5, 6, 7, 8, 9 -> {
                //ignore wall decorations
            }
            //game object
            case 10, 11 -> {
                if (interactType != 0) {
                    addGameObject(z, x, y, sizeX, sizeY, false);
                }
            }
            //floor decoration
            case 22 -> {
                if (interactType == 1) {
                    addGameObject(z, x, y, sizeX, sizeY, true);
                }
            }
            default -> {
                //
                if (locationType >= 12 && locationType <= 21) {
                    if (interactType != 0) {
                        addGameObject(z, x, y, sizeX, sizeY, false);
                    }
                }

                throw new IllegalArgumentException("require:  0 =< locationType <= 22, found:" + locationType);
            }
        }
    }

    public void addFlag(int z, int x, int y, int flag) {
        if (!(z >= 0 && x >= 0 && y >= 0)) {
            throw new IllegalArgumentException(String.format("require: z >= 0 && x >= 0 && y >= 0, found: x:%d y:%d z:%d", x, y, z));
        }

        flags[z][x][y] |= (flag & TileFlags.INITIALIZED);
    }

    @VisibleForTesting
    private void addGameObject(int z, int x, int y, int sizeX, int sizeY, boolean isFloorDecoration) {
        if (!(sizeX >= 1 && sizeY >= 1)) {
            throw new IllegalArgumentException(String.format("require: sizeX >= 1 && sizeY >= 1, found: x:%d y:%d", sizeX, sizeY));
        }

        for (var i = x; i < x + sizeX; x++) {
            for (var j = y; j < y + sizeY; y++) {
                addFlag(z, i, j, isFloorDecoration ? TileFlags.FLOOR_DECORATION : TileFlags.OBJECT);
            }
        }
    }

    /**
     * adds flags for a wall object and opposing flags.
     * i.e. if a wall blocks movement west, then it will set flags on the west tile to block east
     */
    @VisibleForTesting
    private void addWallObject(int z, int x, int y, int locationType, int orientation) {
        if (!(locationType >= 0 && locationType <= 3 && orientation >= 0 && orientation <= 3)) {
            throw new IllegalArgumentException(String.format("require: 3 >= locationType >= 0, 3 >= orientation >= 0, found: x:%d y:%d locationType:%d orientation:%d", x, y, locationType, orientation));
        }

        method3878(z, x, y, locationType, orientation);
    }

    /**
     * from decompiled game
     */
    private void method3878(int z, int x, int y, int type, int orientation) {
        switch (type) {
            case 0 -> {
                if (orientation == 0) {
                    addFlag(z, x, y, 128);
                    addFlag(z, x - 1, y, 8);
                }
                if (orientation == 1) {
                    addFlag(z, x, y, 2);
                    addFlag(z, x, y + 1, 32);
                }
                if (orientation == 2) {
                    addFlag(z, x, y, 8);
                    addFlag(z, x + 1, y, 128);
                }
                if (orientation == 3) {
                    addFlag(z, x, y, 32);
                    addFlag(z, x, y - 1, 2);
                }
            }
            case 1, 3 -> {
                if (orientation == 0) {
                    addFlag(z, x, y, 1);
                    addFlag(z, x - 1, y + 1, 16);
                }
                if (orientation == 1) {
                    addFlag(z, x, y, 4);
                    addFlag(z, x + 1, y + 1, 64);
                }
                if (orientation == 2) {
                    addFlag(z, x, y, 16);
                    addFlag(z, x + 1, y - 1, 1);
                }
                if (orientation == 3) {
                    addFlag(z, x, y, 64);
                    addFlag(z, x - 1, y - 1, 4);
                }
            }
            case 2 -> {
                if (orientation == 0) {
                    addFlag(z, x, y, 130);
                    addFlag(z, x - 1, y, 8);
                    addFlag(z, x, y + 1, 32);
                }
                if (orientation == 1) {
                    addFlag(z, x, y, 10);
                    addFlag(z, x, y + 1, 32);
                    addFlag(z, x + 1, y, 128);
                }
                if (orientation == 2) {
                    addFlag(z, x, y, 40);
                    addFlag(z, x + 1, y, 128);
                    addFlag(z, x, y - 1, 2);
                }
                if (orientation == 3) {
                    addFlag(z, x, y, 160);
                    addFlag(z, x, y - 1, 2);
                    addFlag(z, x - 1, y, 8);
                }
            }
        }
    }
}

//below are from decompiled game.  for reference
/*
public void addGameObject(int x, int y, int xSize, int ySize, boolean blocksLineOfSight) {
    int flag = 256;
    if (blocksLineOfSight) {
        flag += 131072;
    }

    x -= this.xInset;
    y -= this.yInset;

    for (int i = x; i < xSize + x; ++i) {
        if (i >= 0 && i < this.xSize) {
            for (int j = y; j < y + ySize; ++j) {
                if (j >= 0 && j < this.ySize) {
                    this.setFlag(i, j, flag);
                }
            }
        }
    }
}

//some collision flag updates happen when render flags are updated.  not sure exactly if some object is rendered or not.
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

//add object to Scene and CollisionMap
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
