package github.dqw4w9wgxcq.pathfinder.graph.edge;

public record TeleportEdge() implements Edge {
    @Override
    public int cost() {
        return 10;
    }
}
