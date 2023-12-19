package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TeleportLink(
        Type type,
        int id,
        @Nullable Position origin,
        Position destination,
        List<Requirement> requirements
) implements Link {
    public TeleportLink(int id, Position destination, List<Requirement> requirements) {
        this(Type.TELEPORT, id, null, destination, requirements);
    }

    @Override
    public int cost() {
        return 10;
    }
}
