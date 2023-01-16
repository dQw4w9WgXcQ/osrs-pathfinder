package github.dqw4w9wgxcq.pathfinder.graph.domain.link;

import github.dqw4w9wgxcq.pathfinder.graph.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Requirement;

import java.util.List;

public interface Link {
    /**
     * sequential id of the link
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
