package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record ShipLink(Type type, int id, Position start, Position end) implements Link {
    public ShipLink(int id, Position start, Position end) {
        this(Type.SHIP, id, start, end);
    }

    @Override
    public int cost() {
        return 30;
    }

    @Override
    public List<Requirement> requirements() {
        return List.of();
    }
}
