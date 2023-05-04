package dev.dqw4w9wgxcq.pathfinder.pathfinding.edge;

public record TeleportEdge() implements Edge {
    @Override
    public int cost() {
        return 10;
    }
}
