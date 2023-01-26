package github.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Agent;

public record ItemRequirement(int id, int amount) implements Requirement {
    @Override
    public boolean test(Agent agent) {
        return agent.items().containsKey(id) && agent.items().get(id) >= amount;
    }
}
