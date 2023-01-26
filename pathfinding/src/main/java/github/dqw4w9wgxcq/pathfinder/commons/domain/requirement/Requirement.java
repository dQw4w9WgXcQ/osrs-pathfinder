package github.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Agent;

public interface Requirement {
    boolean test(Agent agent);
}
