package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

public record TeleportEdge() implements Edge {
    @Override
    public int cost() {
        return 10;
    }
}
