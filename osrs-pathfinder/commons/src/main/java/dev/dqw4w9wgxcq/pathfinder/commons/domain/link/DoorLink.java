package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

/**
 * A simple door(or gate etc.).  Doors with requirements or that are unidirectional are SpecialLinks.
 */
public record DoorLink(Type type, int id, Position start, Position end, int objectId) implements Link {
    public DoorLink(int id, Position start, Position end, int objectId) {
        this(Type.DOOR, id, start, end, objectId);
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
