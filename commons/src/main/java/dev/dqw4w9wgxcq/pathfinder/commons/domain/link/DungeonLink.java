package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record DungeonLink(Type type, int id, Position origin, Position destination, int objectId, String action)
        implements Link {
    public DungeonLink(int id, Position origin, Position destination, int objectId, String action) {
        this(Type.DOOR, id, origin, destination, objectId, action);
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
