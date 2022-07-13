package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bitwise flags to game uses to determine if a tile is walkable.
 * <p>
 * i.e. flag = 0b00000001; flag &= 0b00000100; if (flag & 0b00000100) { //we can/cant walk }
 */
public class TileFlags {
    public static final int NW = 1;
    public static final int N = 1 << 1;//2
    public static final int NE = 1 << 2;//4
    public static final int E = 1 << 3;//8
    public static final int SE = 1 << 4;//16
    public static final int S = 1 << 5;//32
    public static final int SW = 1 << 6;//64
    public static final int W = 1 << 7;//128

    public static final int OBJECT = 1 << 8;//256
    public static final int FLOOR_DECORATION = 1 << 18;//262144
    public static final int FLOOR = 1 << 21;//2097152
    public static final int ANY_FULL = OBJECT | FLOOR_DECORATION | FLOOR;//2359552

    public static final int VALID = 1 << 24;//16777216

    /**
     * in the game client, index 0 and the last 5 of the flags are filled on init.
     * this is done to create a boarder and because an object with sizeX/Y > 1 from outside the scene could block movement<p>
     * from decompiled game client:
     * <pre>
     * for (int x = 0; x < this.xSize; ++x)
     * {
     *     for (int y = 0; y < this.ySize; ++y)
     *     {
     *         if (x == 0 || y == 0 || x >= this.xSize - 5 || y >= this.ySize - 5)
     *         {
     *             this.flags[x][y] = 16777215;//SCENE_BORDER
     *         }
     *         else
     *         {
     *             this.flags[x][y] = 16777216;//VALID
     *         }
     *     }
     * }
     * </pre>
     */
    public static final int SCENE_BORDER = VALID - 1;//16777215

    //not found in game, used by me to mark tile as initialized
    public static final int VISITED = Integer.MIN_VALUE;

    public static int getOpposite(int plainFlag) {
        return switch (plainFlag) {
            case NW -> SE;
            case N -> S;
            case NE -> SW;
            case E -> W;
            case SE -> NW;
            case S -> N;
            case SW -> NE;
            case W -> E;
            default -> throw new IllegalArgumentException("Invalid flag: " + plainFlag);
        };
    }

    private static final Map<Integer, String> flagNames = Map.ofEntries(
            Map.entry(NW, "NW"),
            Map.entry(N, "N"),
            Map.entry(NE, "NE"),
            Map.entry(E, "E"),
            Map.entry(SE, "SE"),
            Map.entry(S, "S"),
            Map.entry(SW, "SW"),
            Map.entry(W, "W"),
            Map.entry(OBJECT, "OBJECT"),
            Map.entry(FLOOR_DECORATION, "FLOOR_DECORATION"),
            Map.entry(FLOOR, "FLOOR"),
            Map.entry(ANY_FULL, "ANY_FULL"),
            Map.entry(VALID, "VALID"),
            Map.entry(SCENE_BORDER, "SCENE_BORDER"),
            Map.entry(VISITED, "VISITED")
    );

    public static String getFlagName(int flag) {
        var name = flagNames.get(flag);
        if (name == null) {
            throw new IllegalArgumentException("Invalid flag: " + flag);
        }
        return name;
    }

    public static List<String> getFlagNames(int flag) {
        if (flag == 0){
            return List.of("NONE");
        }

        if(flag == SCENE_BORDER){
            return List.of("SCENE_BORDER");
        }

        var list = new ArrayList<String>();
        for (var e : flagNames.entrySet()) {
            if ((e.getKey() & flag) == e.getKey()) {
                var value = e.getValue();
                list.add(value);
            }
        }
        return list;
    }
}
