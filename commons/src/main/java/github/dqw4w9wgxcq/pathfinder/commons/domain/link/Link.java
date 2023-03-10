package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;

import java.util.List;

public interface Link {
    /**
     * index of the link
     */
    int id();

    Position origin();

    Position destination();

    /**
     * approximate cost in terms of walking tile units
     */
    int cost();

    List<Requirement> requirements();
}
