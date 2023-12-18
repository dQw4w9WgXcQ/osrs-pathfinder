package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import net.runelite.api.Quest;

public record QuestRequirement(Quest quest) implements Requirement {
    @Override
    public Type type() {
        return Type.QUEST;
    }

    @Override
    public boolean test(Agent agent) {
        if (agent.quests() == null) return true;

        return agent.quests().contains(quest);
    }
}
