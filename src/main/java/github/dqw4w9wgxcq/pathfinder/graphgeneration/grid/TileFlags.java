package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;

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
    public static final int ANY_FULL = OBJECT | FLOOR_DECORATION | FLOOR;

    public static final int HAS_FLAGS = 1 << 24;//16777216

    /**
     * in the game client, index 0 and the last 5 of the flags are filled on init.
     * this is done to create a boarder and because an object with sizeX/Y > 1 from outside the scene could block movement<p>
     * from decompile:
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
     *             this.flags[x][y] = 16777216;//HAS_FLAGS
     *         }
     *     }
     * }
     * </pre>
     */
    public static final int SCENE_BORDER = HAS_FLAGS - 1;//16777215

    //not found in game, used to mark tile as initialized
    public static final int INITIALIZED = Integer.MIN_VALUE;
}
