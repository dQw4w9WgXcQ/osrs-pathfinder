package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public sealed interface Requirement permits ItemRequirement, QuestRequirement {
    Type type();

    boolean check(Agent agent);

    default Agent affectAgent(Agent agent) {
        return agent;
    }

    @RequiredArgsConstructor
    enum Type {
        ITEM(ItemRequirement.class),
        QUEST(QuestRequirement.class),
        ;

        @Getter
        private final Class<? extends Requirement> clazz;
    }
}
