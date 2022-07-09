package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge;

public record DoorEdge(int destinationId) implements Edge {
    @Override
    public String getType() {
        return "door";
    }
}
