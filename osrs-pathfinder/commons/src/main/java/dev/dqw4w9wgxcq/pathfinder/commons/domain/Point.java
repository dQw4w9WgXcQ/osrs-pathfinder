package dev.dqw4w9wgxcq.pathfinder.commons.domain;

// overridden equals/hashcode for performance
public record Point(int x, int y) {
    @Override
    public boolean equals(Object obj) {
        assert obj instanceof Point;

        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return pack();
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int pack() {
        return pack(x, y);
    }

    public static Point unpack(int packed) {
        return new Point(unpackX(packed), unpackY(packed));
    }

    public static int unpackX(int packed) {
        return packed & Short.MAX_VALUE;
    }

    public static int unpackY(int packed) {
        return packed >> 16;
    }

    public static int pack(int x, int y) {
        return (x & Short.MAX_VALUE) | y << 16;
    }
}