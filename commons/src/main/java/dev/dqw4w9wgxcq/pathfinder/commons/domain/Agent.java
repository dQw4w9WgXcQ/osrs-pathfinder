package dev.dqw4w9wgxcq.pathfinder.commons.domain;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import lombok.With;
import net.runelite.api.Quest;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

// items/quests null = pass all requirements
@With
public record Agent(int magicLevel, @Nullable Map<Integer, Integer> items, @Nullable Set<Quest> quests) {
    public boolean checkRequirements(List<Requirement> requirements) {
        return requirements.stream().allMatch(r -> r.check(this));
    }

    public Agent affect(List<Requirement> requirements) {
        var agent = this;
        for (var req : requirements) {
            agent = req.affectAgent(agent);
        }
        return agent;
    }
}