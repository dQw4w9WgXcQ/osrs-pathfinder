package dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import dev.dqw4w9wgxcq.pathfinder.commons.TileFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WallDirection {
    N(TileFlags.N_WALL, 0, 1),
    E(TileFlags.E_WALL, 1, 0),
    S(TileFlags.S_WALL, 0, -1),
    W(TileFlags.W_WALL, -1, 0),
    ;

    @Getter
    private final int flag;

    @Getter
    private final int dx, dy;

    public int oppositeFlag() {
        return TileFlags.getOpposite(flag);
    }

    public static WallDirection fromDXY(int dx, int dy) {
        if (dx == 0) {
            if (dy == 1) {
                return N;
            } else if (dy == -1) {
                return S;
            } else {
                throw new IllegalArgumentException("dy must be 1 or -1 if x is 0");
            }
        } else if (dy == 0) {
            if (dx == 1) {
                return E;
            } else if (dx == -1) {
                return W;
            } else {
                throw new IllegalArgumentException("dx must be 1 or -1 if y is 0");
            }
        } else {
            throw new IllegalArgumentException("neither dy or dx is 0");
        }
    }
}
