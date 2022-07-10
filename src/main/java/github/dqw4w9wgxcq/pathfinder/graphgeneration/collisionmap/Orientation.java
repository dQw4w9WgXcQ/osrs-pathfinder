package github.dqw4w9wgxcq.pathfinder.graphgeneration.collisionmap;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Orientation {
    W(0, TileFlags.W),
    N(1, TileFlags.N),
    E(2, TileFlags.E),
    S(3, TileFlags.S),
    ;

    public final int config;
    public final int associatedFlag;
}
