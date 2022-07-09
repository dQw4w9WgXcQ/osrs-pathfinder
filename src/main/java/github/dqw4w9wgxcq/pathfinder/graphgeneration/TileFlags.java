package github.dqw4w9wgxcq.pathfinder.graphgeneration;

public class TileFlags {
    public static final int NW = 1;
    public static final int N = 0x2;// 1 << 1
    public static final int NE = 0x4;// 1 << 2
    public static final int E = 0x8;// 1 << 3
    public static final int SE = 0x10;// 1 << 4
    public static final int S = 0x20;// 1 << 5
    public static final int SW = 0x40;// 1 << 6
    public static final int W = 0x80;// 1 << 7

    public static final int OBJECT = 0x100;// 1 << 8
    public static final int FLOOR_DECORATION = 0x40000;// 1 << 18
    public static final int FLOOR = 0x200000;// 1 << 21
    public static final int FULL = OBJECT | FLOOR_DECORATION | FLOOR;

    //line of sight
    public static final int LOS_N = N << 9; // 0x400
    public static final int LOS_E = E << 9; // 0x1000
    public static final int LOS_S = S << 9; // 0x4000
    public static final int LOS_W = W << 9; // 0x10000
    public static final int LOS_FULL = 0x20000;// 1 << 17;

    public static final int VISITED = Integer.MIN_VALUE;
}
