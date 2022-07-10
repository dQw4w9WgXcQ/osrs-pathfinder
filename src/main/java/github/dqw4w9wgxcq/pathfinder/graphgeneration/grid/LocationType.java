//package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;
//
//import lombok.AllArgsConstructor;
//
//@AllArgsConstructor
//public enum LocationType {
//    WALL_OBJECT_0(),
//    WALL_OBJECT_1(),
//    WALL_OBJECT_2(),
//    WALL_OBJECT_3(),
//    WALL_DECORATION_4(),
//    WALL_DECORATION_5(),
//    WALL_DECORATION_6(),
//    WALL_DECORATION_7(),
//    WALL_DECORATION_8(),
//    GAME_OBJECT_9(),
//    GAME_OBJECT_10_11(),
//    GAME_OBJECT_12_TO_21(),
//    FLOOR_DECORATION_22(),
//    ;
//
//    public static LocationType getType(int type) {
//        return switch (type) {
//            case 0 -> WALL_OBJECT_0;
//            case 1 -> WALL_OBJECT_1;
//            case 2 -> WALL_OBJECT_2;
//            case 3 -> WALL_OBJECT_3;
//            case 4 -> WALL_DECORATION_4;
//            case 5 -> WALL_DECORATION_5;
//            case 6 -> WALL_DECORATION_6;
//            case 7 -> WALL_DECORATION_7;
//            case 8 -> WALL_DECORATION_8;
//            case 9 -> GAME_OBJECT_9;
//            case 10, 11 -> GAME_OBJECT_10_11;
//            case 22 -> FLOOR_DECORATION_22;
//            default -> {
//                if (type >= 12 && type <= 21) {
//                    yield GAME_OBJECT_12_TO_21;
//                }
//
//                throw new IllegalArgumentException("require:  0 =< type <= 22, found:" + type);
//            }
//        };
//    }
//}

//from decompile for reference
/*
    final void method3050(final int z,
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

		final Renderable renderable;
		if (locationType == 22)
		{ // L: 994
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
				collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.boolean1);
			}
		}
		else
		{
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
						collisionMap.method3878(x, y, locationType, orientation, objectDefinition.boolean1);
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
						collisionMap.method3878(x, y, locationType, orientation, objectDefinition.boolean1);
					}

					break;
				case 2:  // L: 1034
					int xd = orientation + 1 & 3; // L: 1035

					Object var25;
					Object var31;
					if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
					{ // L: 1038
						var31 = objectDefinition.getModel(2, orientation + 4, tileHeights, localX, tileHeight, localY); // L: 1039
						var25 = objectDefinition.getModel(2, xd, tileHeights, localX, tileHeight, localY); // L: 1040
					}
					else
					{
						var31 = new DynamicObject(id, 2, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1043
						var25 = new DynamicObject(id, 2, xd, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1044
					}

					scene.newBoundaryObject(z, x, y, tileHeight, (Renderable) var31, (Renderable) var25, Tiles.field1007[orientation], Tiles.field1007[xd], tag, objectFlags); // L: 1046

					if (objectDefinition.interactType != 0)
					{ // L: 1047
						collisionMap.method3878(x, y, locationType, orientation, objectDefinition.boolean1);
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
						collisionMap.method3878(x, y, locationType, orientation, objectDefinition.boolean1);
					}

					break;
				case 4:  // L: 1066
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
				case 5:
				{ // L: 1073
					int var23 = 16; // L: 1074

					long var24 = scene.getBoundaryObjectTag(z, x, y); // L: 1075

					if (0L != var24)
					{ // L: 1076
						var23 = class162.getObjectDefinition(Decimator.Entity_unpackID(var24)).int2;
					}
					Renderable renderable2;
					if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
					{ // L: 1078
						renderable2 = objectDefinition.getModel(4, orientation, tileHeights, localX, tileHeight, localY);
					}
					else
					{
						renderable2 = new DynamicObject(id, 4, orientation, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1079
					}

					scene.newWallDecoration(z, x, y, tileHeight, renderable2, null, Tiles.field1007[orientation], 0, var23 * Tiles.field1003[orientation], var23 * Tiles.field1005[orientation], tag, objectFlags); // L: 1080

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

					Renderable renderable2;
					if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
					{ // L: 1088
						renderable2 = objectDefinition.getModel(4, orientation + 4, tileHeights, localX, tileHeight, localY);
					}
					else
					{
						renderable2 = new DynamicObject(id, 4, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1089
					}

					scene.newWallDecoration(z, x, y, tileHeight, renderable2, null, 256, orientation, var23 * Tiles.field1006[orientation], var23 * Tiles.field1002[orientation], tag, objectFlags); // L: 1090

					break;
				}
				case 7:  // L: 1093
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
				case 8:
				{ // L: 1101
					int var23 = 8; // L: 1102

					long var24 = scene.getBoundaryObjectTag(z, x, y); // L: 1103

					if (var24 != 0L)
					{ // L: 1104
						var23 = class162.getObjectDefinition(Decimator.Entity_unpackID(var24)).int2 / 2;
					}

					int var28 = orientation + 2 & 3; // L: 1107

					Renderable var27;
					Renderable renderable2;
					if (objectDefinition.animationId == -1 && objectDefinition.transforms == null)
					{ // L: 1108
						renderable2 = objectDefinition.getModel(4, orientation + 4, tileHeights, localX, tileHeight, localY); // L: 1109
						var27 = objectDefinition.getModel(4, var28 + 4, tileHeights, localX, tileHeight, localY); // L: 1110
					}
					else
					{
						renderable2 = new DynamicObject(id, 4, orientation + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1113
						var27 = new DynamicObject(id, 4, var28 + 4, z2, x, y, objectDefinition.animationId, objectDefinition.boolean3, null); // L: 1114
					}

					scene.newWallDecoration(z, x, y, tileHeight, renderable2, var27, 256, orientation, var23 * Tiles.field1006[orientation], var23 * Tiles.field1002[orientation], tag, objectFlags); // L: 1116

					break;
				}
				case 9:  // L: 1058
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
						collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.boolean1);
					}

					break;
				case 10:
				case 11:
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
						collisionMap.addGameObject(x, y, sizeX, sizeY, objectDefinition.boolean1);
					}

					break;
			}
		}
	}
 */