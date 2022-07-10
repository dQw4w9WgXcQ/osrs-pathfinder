package github.dqw4w9wgxcq.pathfinder.graphgeneration.grid;

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

    public static Orientation forConfig(int config) {
        var out = Orientation.values()[config];

        assert out.config == config;

        return out;
    }
}
