package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

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
    public static final int NW = 1;
    public static final int N = 1 << 1;//2
    public static final int NE = 1 << 2;//4
    public static final int E = 1 << 3;//8
    public static final int SE = 1 << 4;//16
    public static final int S = 1 << 5;//32
    public static final int SW = 1 << 6;//64
    public static final int W = 1 << 7;//128

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
            case NW -> SE;
            case N -> S;
            case NE -> SW;
            case E -> W;
            case SE -> NW;
            case S -> N;
            case SW -> NE;
            case W -> E;
            default -> throw new IllegalArgumentException("not a cardinal flag: " + cardinalFlag);
        };
    }

    private static final Map<Integer, String> flagDescs = Map.ofEntries(
            Map.entry(NW, "NW"),
            Map.entry(N, "N"),
            Map.entry(NE, "NE"),
            Map.entry(E, "E"),
            Map.entry(SE, "SE"),
            Map.entry(S, "S"),
            Map.entry(SW, "SW"),
            Map.entry(W, "W"),
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
            //tile can not have data but still have flags if an object goes off the edge of a region into an unloaded region
            log.debug("found tile without HAVE_DATA flag, but has other flags: " + config + " (" + Integer.toBinaryString(config) + ")");

            list.add("x");
        }

        return list;
    }

    public static String describe(int config) {
        return String.join(",", getDescriptions(config));
    }
}
