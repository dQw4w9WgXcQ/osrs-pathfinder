package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is modeled after the collision map from the decompiled game client, but is global instead of scene.
 * uses bit flags to store collision data and bitwise operations to check for collisions
 */
@Slf4j
public class PlaneGrid {
    @VisibleForTesting
    final int[][] flags;
    @Getter
    private final int sizeX, sizeY;

    public PlaneGrid(int sizeX, int sizeY) {
        log.debug("map inited with size x:" + sizeX + " y:" + sizeY);

        flags = new int[sizeX][sizeY];

        for (var x = 0; x < sizeX; x++) {
            flags[x][0] = CollisionFlags.BORDER;
            flags[x][sizeY - 1] = CollisionFlags.BORDER;
        }

        for (var y = 0; y < sizeY; y++) {
            flags[0][y] = CollisionFlags.BORDER;
            flags[sizeX - 1][y] = CollisionFlags.BORDER;
        }

        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public boolean checkFlag(int x, int y, int mask) {
        return (flags[x][y] & mask) != 0;
    }

    public boolean canTravelInDirection(int x, int y, int dx, int dy) {
        Preconditions.checkArgument(
                dx != 0 || dy != 0,
                "dx and dy cant both be 0, found dx: " + dx + " dy: " + dy
        );
        Preconditions.checkArgument(
                Math.abs(dx) <= 1 && Math.abs(dy) <= 1,
                "dx and dy must be 1 or -1, found dx: " + dx + " dy: " + dy
        );

        var flag = flags[x][y];
        var destinationX = x + dx;
        var destinationY = y + dy;

        if (destinationX < 0 || destinationX >= sizeX || destinationY < 0 || destinationY >= sizeY) {
            log.debug("destination out of bounds," +
                    " destinationX: " + destinationX +
                    " destinationY: " + destinationY);
            return false;
        }

        var destinationFlag = flags[x + dx][y + dy];

        var checkMask = 0;
        if (dx == 1) {
            checkMask |= CollisionFlags.E;

            if (dy == 1) {
                checkMask |= CollisionFlags.NE;
            } else if (dy == -1) {
                checkMask |= CollisionFlags.SE;
            }
        } else if (dx == -1) {
            checkMask |= CollisionFlags.W;

            if (dy == 1) {
                checkMask |= CollisionFlags.NW;
            } else if (dy == -1) {
                checkMask |= CollisionFlags.SW;
            }
        }

        if (dy == 1) {
            checkMask |= CollisionFlags.N;
        } else if (dy == -1) {
            checkMask |= CollisionFlags.S;
        }

        log.trace("check mask: " + checkMask +
                " stringed: " + CollisionFlags.describe(checkMask));

        log.trace("flag: " + CollisionFlags.describe(flag));
        if ((checkMask & flag) != 0) {
            log.trace("wall collision detected");
            return false;
        }

        if ((destinationFlag & CollisionFlags.ANY_FULL) != 0) {
            log.trace("full collision detected");
            return false;
        }

        if((destinationFlag & CollisionFlags.VALID) == 0) {
            log.trace("destination tile doesn't exist");
            return false;
        }

        return true;
    }

    void addFlag(int x, int y, int flag) {
        log.debug("adding flag " + flag + " " + x + "x " + y + "y");

        Preconditions.checkArgument(
                x >= 0 && y >= 0 && x < sizeX && y < sizeY,
                "expected: x <" + sizeX + ", y <" + sizeY + ", found: " + x + "," + y + " flags: " + flag
        );

        flags[x][y] |= (flag | CollisionFlags.VALID);
    }

    void addObjectFlags(int x, int y, int sizeX, int sizeY, boolean isFloorDecoration) {
        log.debug("adding game object at " + x + "," + y + " sizeX:" + sizeX + " sizeY:" + sizeY);

        Preconditions.checkArgument(
                sizeX >= 1 && sizeY >= 1,
                "expected: sizeXY >=1, found: " + sizeX + "," + sizeY
        );

        var flag = isFloorDecoration ? CollisionFlags.FLOOR_DECORATION : CollisionFlags.OBJECT;

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
    void addWallFlags(int x, int y, int locationType, int orientation) {
        log.debug("adding wall object at " + x + "," + y + " locationType:" + locationType + " orientation:" + orientation);

        Preconditions.checkArgument(
                locationType >= 0 && locationType <= 3 && orientation >= 0 && orientation <= 3,
                "expected: 3 >= locationType >= 0, 3 >= orientation >= 0, found: x:%d y:%d locationType:%d orientation:%d", x, y, locationType, orientation
        );

        method3878(x, y, locationType, orientation);
    }

    /**
     * copy pasted from decompiled game client
     */
    private void method3878(int x, int y, int type, int orientation) {
        switch (type) {
            case 0 -> {
                switch (orientation) {
                    case 0 -> {
                        addFlag(x, y, 128);
                        addFlag(x - 1, y, 8);
                    }
                    case 1 -> {
                        addFlag(x, y, 2);
                        addFlag(x, y + 1, 32);
                    }
                    case 2 -> {
                        addFlag(x, y, 8);
                        addFlag(x + 1, y, 128);
                    }
                    case 3 -> {
                        addFlag(x, y, 32);
                        addFlag(x, y - 1, 2);
                    }
                }
            }
            case 1, 3 -> {
                switch (orientation) {
                    case 0 -> {
                        addFlag(x, y, 1);
                        addFlag(x - 1, y + 1, 16);
                    }
                    case 1 -> {
                        addFlag(x, y, 4);
                        addFlag(x + 1, y + 1, 64);
                    }
                    case 2 -> {
                        addFlag(x, y, 16);
                        addFlag(x + 1, y - 1, 1);
                    }
                    case 3 -> {
                        addFlag(x, y, 64);
                        addFlag(x - 1, y - 1, 4);
                    }
                }
            }
            case 2 -> {
                switch (orientation) {
                    case 0 -> {
                        addFlag(x, y, 130);
                        addFlag(x - 1, y, 8);
                        addFlag(x, y + 1, 32);
                    }
                    case 1 -> {
                        addFlag(x, y, 10);
                        addFlag(x, y + 1, 32);
                        addFlag(x + 1, y, 128);
                    }
                    case 2 -> {
                        addFlag(x, y, 40);
                        addFlag(x + 1, y, 128);
                        addFlag(x, y - 1, 2);
                    }
                    case 3 -> {
                        addFlag(x, y, 160);
                        addFlag(x, y - 1, 2);
                        addFlag(x - 1, y, 8);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TileMap{" +
                "width=" + sizeX +
                ", height=" + sizeY +
                '}';
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
