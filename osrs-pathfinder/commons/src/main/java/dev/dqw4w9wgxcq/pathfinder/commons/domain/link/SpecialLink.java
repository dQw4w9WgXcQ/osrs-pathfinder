package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;
import java.util.Map;

public record SpecialLink(
        Type type,
        int id,
        Position start,
        Position end,
        int cost,
        List<Requirement> requirements,
        Map<String, Object> extra)
        implements Link {
    public SpecialLink(
            int id, Position start, Position end, int cost, List<Requirement> requirements, Map<String, Object> extra) {
        this(Type.SPECIAL, id, start, end, cost, requirements, extra);
    }
}
