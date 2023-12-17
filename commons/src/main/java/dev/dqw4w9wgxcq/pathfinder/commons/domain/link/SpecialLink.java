package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;
import java.util.Map;

public record SpecialLink(
        int id,
        Position origin,
        Position destination,
        int cost,
        List<Requirement> requirements,
        Map<String, Object> extra
) implements Link {
    @Override
    public LinkType type() {
        return LinkType.SPECIAL;
    }
}
