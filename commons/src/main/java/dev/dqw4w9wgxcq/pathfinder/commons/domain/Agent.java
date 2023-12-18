package dev.dqw4w9wgxcq.pathfinder.commons.domain;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import lombok.With;
import net.runelite.api.Quest;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

//items/quests null = pass all requirements
@With
public record Agent(int magicLevel, @Nullable Map<Integer, Integer> items, @Nullable Set<Quest> quests) {
    public boolean hasRequirements(List<Requirement> requirements) {
        return requirements.stream().allMatch(r -> r.test(this));
    }
}