package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record TeleportLink(
        int id,
        Position origin,
        Position destination,
        List<Requirement> requirements
) implements Link {
    @Override
    public Type type() {
        return Type.TELEPORT;
    }

    @Override
    public int cost() {
        return 10;
    }
}
