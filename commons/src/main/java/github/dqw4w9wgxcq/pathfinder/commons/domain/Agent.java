package github.dqw4w9wgxcq.pathfinder.commons.domain;

import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import net.runelite.api.Quest;

import java.util.List;
import java.util.Map;

public record Agent(int magicLevel, Map<Integer, Integer> items, List<Quest> quests) {
    public boolean hasRequirements(List<Requirement> requirements) {
        return requirements.stream().allMatch(r -> r.test(this));
    }
}
