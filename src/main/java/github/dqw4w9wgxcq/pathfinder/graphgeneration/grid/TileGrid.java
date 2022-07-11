package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.utils.RegionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Region;

import java.util.List;
import java.util.Map;

/**
 * this class is modeled after the collision map from the decompiled game client, but is global instead of scene.
 * uses bit flags to store collision data and bitwise operations to check for collisions
 */
@Slf4j
public class TileGrid {
    @Getter
    private final int[][] flags;
    @Getter
    private final int xSize;
    @Getter
    private final int ySize;

    public TileGrid(int xSize, int ySize) {
        log.debug("grid inited with size x:" + xSize + " y:" + ySize);

        flags = new int[xSize][ySize];

        for (var x = 0; x < xSize; x++) {
            flags[x][0] = TileFlags.SCENE_BORDER;
            flags[x][ySize - 1] = TileFlags.SCENE_BORDER;
        }

        for (var y = 0; y < ySize; y++) {
            flags[0][y] = TileFlags.SCENE_BORDER;
            flags[xSize - 1][y] = TileFlags.SCENE_BORDER;
        }

        this.xSize = xSize;
        this.ySize = ySize;
    }

    public boolean canTravelInDirection(int x, int y, int dx, int dy) {
        Preconditions.checkArgument(dx != 0 || dy != 0, "dx and dy cant both be 0, found dx: " + dx + " dy: " + dy);
        Preconditions.checkArgument(Math.abs(dx) <= 1 && Math.abs(dy) <= 1, "dx and dy must be 1 or -1, found dx: " + dx + " dy: " + dy);

        var flag = flags[x][y];
        var destinationX = x + dx;
        var destinationY = y + dy;

        if (destinationX < 0 || destinationX >= xSize || destinationY < 0 || destinationY >= ySize) {
            log.trace("canTravelInDirection: destination out of bounds, x: " + destinationX + " y: " + destinationY + " dx: " + dx + " dy: " + dy);
            return false;
        }

        var destinationFlag = flags[x + dx][y + dy];

        var checkMask = 0;
        if (dx == 1) {
            checkMask |= TileFlags.W;

            if (dy == 1) {
                checkMask |= TileFlags.NW;
            } else if (dy == -1) {
                checkMask |= TileFlags.SW;
            }
        } else if (dx == -1) {
            checkMask |= TileFlags.E;

            if (dy == 1) {
                checkMask |= TileFlags.NE;
            } else if (dy == -1) {
                checkMask |= TileFlags.SE;
            }
        }

        if (dy == 1) {
            checkMask |= TileFlags.N;
        } else if (dy == -1) {
            checkMask |= TileFlags.S;
        }

        if ((flag & checkMask) != 0) {
            return false;
        }

        return (destinationFlag & TileFlags.ANY_FULL) != 0;
    }

    public static void addFloorFromRegion(TileGrid[] world, Region region) {
        log.debug("adding region " + region.getRegionID() + " x" + region.getRegionX() + "y" + region.getRegionY());
        var baseX = region.getBaseX();
        var baseY = region.getBaseY();

        for (var z = 0; z < world.length; z++) {
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
                            world[modifiedZ].addFlag(x + baseX, y + baseY, TileFlags.FLOOR);
                        }
                    }
                }
            }
        }
    }

    public static void addObjectLocations(TileGrid[] world, List<Location> locations, Map<Integer, ObjectDefinition> definitions) {
        for (var location : locations) {
            world[location.getPosition().getZ()].addObjectLocation(location, definitions);
        }
    }

    /**
     * @param location object spawn location
     */
    public void addObjectLocation(Location location, Map<Integer, ObjectDefinition> definitions) {
        log.trace("adding location " + location);

        var position = location.getPosition();

        var x = position.getX();
        var y = position.getY();

        var objectDefinition = definitions.get(location.getId());

        log.trace("objlocation name : " + objectDefinition.getName());

        var orientation = location.getOrientation();

        //rotate according to the orientation
        int sizeX;
        int sizeY;
        if (orientation == 1 || orientation == 3) {
            sizeX = objectDefinition.getSizeY(); // L: 960
            sizeY = objectDefinition.getSizeX(); // L: 961
        } else { // L: 959
            sizeX = objectDefinition.getSizeX(); // L: 964
            sizeY = objectDefinition.getSizeY(); // L: 965
        }

        var locationType = location.getType();
        var interactType = objectDefinition.getInteractType();
        switch (locationType) {
            //wall objects
            case 0, 1, 2, 3 -> {
                if (interactType != 0) {
                    addWallObject(x, y, locationType, orientation);
                }
            }
            //wall decoration
            case 4, 5, 6, 7, 8, 9 -> {
                //ignore wall decorations
            }
            //game object
            case 10, 11 -> {
                if (interactType != 0) {
                    addGameObject(x, y, sizeX, sizeY, false);
                }
            }
            //floor decoration
            case 22 -> {
                if (interactType == 1) {
                    addGameObject(x, y, sizeX, sizeY, true);
                }
            }
            default -> {
                //
                if (locationType >= 12 && locationType <= 21) {
                    if (interactType != 0) {
                        addGameObject(x, y, sizeX, sizeY, false);
                    }
                } else {
                    throw new IllegalArgumentException("expect:  0 =< locationType <= 22, found:" + locationType);
                }
            }
        }
    }

    public void addFlag(int x, int y, int flag) {
        log.trace("adding flag " + flag + "x" + x + "y" + y);

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < xSize && y < ySize,
                "expect: 0 <= z, x, y <" + xSize + "," + ySize + ", found: " + x + "," + y
        );

        flags[x][y] |= (flag & TileFlags.VISITED);
    }

    private void addGameObject(int x, int y, int sizeX, int sizeY, boolean isFloorDecoration) {
        log.trace("adding game object at " + x + "," + y + " sizeX:" + sizeX + " sizeY:" + sizeY);

        Preconditions.checkArgument(
                sizeX >= 1 && sizeY >= 1,
                "expect sizeXY >=1, found: " + sizeX + "," + sizeY
        );

        var flag = isFloorDecoration ? TileFlags.FLOOR_DECORATION : TileFlags.OBJECT;

        for (var i = x; i < x + sizeX; i++) {
            for (var j = y; j < y + sizeY; j++) {
                //todo, i think can fail if the object size goes off the map (throws index out of bounds exception) but haven't seen it yet
                addFlag(i, j, flag);
            }
        }
    }

    /**
     * adds flags for a wall object and opposing flags.
     * i.e. if a wall blocks movement west, then it will set flags on the west tile to block east
     */
    private void addWallObject(int x, int y, int locationType, int orientation) {
        log.trace("adding wall object at " + x + "," + y + " locationType:" + locationType + " orientation:" + orientation);

        Preconditions.checkArgument(
                locationType >= 0 && locationType <= 3 && orientation >= 0 && orientation <= 3,
                "expect: 3 >= locationType >= 0, 3 >= orientation >= 0, found: x:%d y:%d locationType:%d orientation:%d", x, y, locationType, orientation
        );

        method3878(x, y, locationType, orientation);
    }

    /**
     * copy pasted from decompiled game client
     */
    private void method3878(int x, int y, int type, int orientation) {
        switch (type) {
            case 0 -> {
                if (orientation == 0) {
                    addFlag(x, y, 128);
                    addFlag(x - 1, y, 8);
                }
                if (orientation == 1) {
                    addFlag(x, y, 2);
                    addFlag(x, y + 1, 32);
                }
                if (orientation == 2) {
                    addFlag(x, y, 8);
                    addFlag(x + 1, y, 128);
                }
                if (orientation == 3) {
                    addFlag(x, y, 32);
                    addFlag(x, y - 1, 2);
                }
            }
            case 1, 3 -> {
                if (orientation == 0) {
                    addFlag(x, y, 1);
                    addFlag(x - 1, y + 1, 16);
                }
                if (orientation == 1) {
                    addFlag(x, y, 4);
                    addFlag(x + 1, y + 1, 64);
                }
                if (orientation == 2) {
                    addFlag(x, y, 16);
                    addFlag(x + 1, y - 1, 1);
                }
                if (orientation == 3) {
                    addFlag(x, y, 64);
                    addFlag(x - 1, y - 1, 4);
                }
            }
            case 2 -> {
                if (orientation == 0) {
                    addFlag(x, y, 130);
                    addFlag(x - 1, y, 8);
                    addFlag(x, y + 1, 32);
                }
                if (orientation == 1) {
                    addFlag(x, y, 10);
                    addFlag(x, y + 1, 32);
                    addFlag(x + 1, y, 128);
                }
                if (orientation == 2) {
                    addFlag(x, y, 40);
                    addFlag(x + 1, y, 128);
                    addFlag(x, y - 1, 2);
                }
                if (orientation == 3) {
                    addFlag(x, y, 160);
                    addFlag(x, y - 1, 2);
                    addFlag(x - 1, y, 8);
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
