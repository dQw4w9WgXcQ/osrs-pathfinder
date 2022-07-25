package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bitwise flags to game client uses to determine if a tile is walkable.
 * <p>
 * i.e. flag = 0b00000001; flag &= 0b00000100; if (flag & 0b00000100) { //we can/cant walk }
 */
@Slf4j
public class TileFlags {
    //wall
    public static final int NW_WALL = 1;
    public static final int N_WALL = 1 << 1;//2
    public static final int NE_WALL = 1 << 2;//4
    public static final int E_WALL = 1 << 3;//8
    public static final int SE_WALL = 1 << 4;//16
    public static final int S_WALL = 1 << 5;//32
    public static final int SW_WALL = 1 << 6;//64
    public static final int W_WALL = 1 << 7;//128

    //object
    public static final int OBJECT = 1 << 8;//256
    public static final int FLOOR_DECORATION = 1 << 18;//262144
    public static final int FLOOR = 1 << 21;//2097152
    public static final int ANY_FULL = OBJECT | FLOOR_DECORATION | FLOOR;//2359552

    //marker
    //this flag is used in the game client to represent "valid" tiles in a loaded scene
    public static final int HAVE_DATA = 1 << 24;//16777216

    public static int getOpposite(int cardinalFlag) {
        return switch (cardinalFlag) {
            case NW_WALL -> SE_WALL;
            case N_WALL -> S_WALL;
            case NE_WALL -> SW_WALL;
            case E_WALL -> W_WALL;
            case SE_WALL -> NW_WALL;
            case S_WALL -> N_WALL;
            case SW_WALL -> NE_WALL;
            case W_WALL -> E_WALL;
            default -> throw new IllegalArgumentException("not a cardinal flag: " + cardinalFlag);
        };
    }

    private static final Map<Integer, String> flagDescs = Map.ofEntries(
            Map.entry(NW_WALL, "Nw"),
            Map.entry(N_WALL, "N"),
            Map.entry(NE_WALL, "Ne"),
            Map.entry(E_WALL, "E"),
            Map.entry(SE_WALL, "Se"),
            Map.entry(S_WALL, "S"),
            Map.entry(SW_WALL, "Sw"),
            Map.entry(W_WALL, "W"),
            Map.entry(OBJECT, "o"),
            Map.entry(FLOOR_DECORATION, "d"),
            Map.entry(FLOOR, "f"),
            Map.entry(HAVE_DATA, ".")
    );

    static String getDescriptionForFlag(int flag) {
        var name = flagDescs.get(flag);
        if (name == null) {
            throw new IllegalArgumentException("not a flag: " + flag + " (" + Integer.toBinaryString(flag) + ")");
        }
        return name;
    }

    @VisibleForTesting
    static List<String> getDescriptions(int config) {
        if (config == 0) {
            return List.of("?");
        }

        if (config == HAVE_DATA) {
            return List.of(".");
        }

        var list = new ArrayList<String>();
        for (var e : flagDescs.entrySet()) {
            if ((e.getKey() & config) == e.getKey()) {
                if (e.getKey() == HAVE_DATA) {
                    continue;
                }
                var value = e.getValue();
                list.add(value);
            }
        }

        var loaded = (config & HAVE_DATA) == HAVE_DATA;
        if (!loaded) {
            //tile can be not marked as have data but still have flags if an object goes off the edge of a region into an unloaded region
            log.debug("found tile without HAVE_DATA flag, but has other flags: " + config + " (" + Integer.toBinaryString(config) + ")");

            list.add("x");
        }

        return list;
    }

    public static String describe(int config) {
        return String.join("", getDescriptions(config));
    }
}
