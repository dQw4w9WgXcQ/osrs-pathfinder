package dev.dqw4w9wgxcq.pathfinder.commons.domain;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import net.runelite.api.Quest;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

//items/quests null = pass all requirements
public record Agent(int magicLevel, @Nullable Map<Integer, Integer> items, @Nullable List<Quest> quests) {
    public boolean hasRequirements(List<Requirement> requirements) {
        return requirements.stream().allMatch(r -> r.test(this));
    }
}