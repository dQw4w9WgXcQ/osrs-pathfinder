package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;

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
}
