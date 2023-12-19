package dev.dqw4w9wgxcq.pathfinder.commons.domain.step;

public sealed interface Step permits WalkStep, LinkStep {
    enum Type {
        WALK,
        LINK
    }
}