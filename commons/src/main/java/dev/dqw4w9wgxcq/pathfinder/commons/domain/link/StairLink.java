package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record StairLink(Type type, int id, Position origin, Position destination, int objectId, boolean up)
        implements Link {
    public StairLink(int id, Position origin, Position destination, int objectId, boolean up) {
        this(Type.STAIR, id, origin, destination, objectId, up);
    }

    @Override
    public int cost() {
        return 10;
    }

    @Override
    public List<Requirement> requirements() {
        return List.of();
    }
}
