package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;

public sealed interface Requirement permits ItemRequirement, QuestRequirement {
    boolean test(Agent agent);
}
