package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record WildernessDitchLink(
        int id,
        Position origin,
        Position destination,
        int cost,
        List<Requirement> requirements
) implements Link {

}
