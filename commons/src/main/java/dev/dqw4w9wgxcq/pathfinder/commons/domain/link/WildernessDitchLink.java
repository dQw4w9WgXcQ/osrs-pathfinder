package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record WildernessDitchLink(
        Type type,
        int id,
        Position origin,
        Position destination
) implements Link {
    public WildernessDitchLink(int id, Position origin, Position destination) {
        this(Type.WILDERNESS_DITCH, id, origin, destination);
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
