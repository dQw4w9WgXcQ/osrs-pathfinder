package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import net.runelite.api.Quest;

public record QuestRequirement(Type type, Quest quest) implements Requirement {
    public QuestRequirement(Quest quest) {
        this(Type.QUEST, quest);
    }

    @Override
    public boolean check(Agent agent) {
        if (agent.quests() == null) return true;

        return agent.quests().contains(quest);
    }
}
