package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;

public record ItemRequirement(int id, int amount) implements Requirement {
    @Override
    public boolean test(Agent agent) {
        if (agent.items() == null) return true;

        return agent.items().containsKey(id) && agent.items().get(id) >= amount;
    }
}
