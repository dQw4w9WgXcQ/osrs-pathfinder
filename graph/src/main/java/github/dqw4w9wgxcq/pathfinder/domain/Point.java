package github.dqw4w9wgxcq.pathfinder.domain;

public record Point(int x, int y) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point p) {
            return x == p.x && y == p.y;
        }
        return false;
    }

    public int packed() {
        return (x & 32767) | (y & 32767) << 16;
    }
    public static Point fromInt(int packed) {
        return new Point(packed & 32767, packed >> 16);
    }

    public static int xFromInt(int packed) {
        return packed & 32767;
    }

    public static int yFromInt(int packed) {
        return packed >> 16;
    }
}
