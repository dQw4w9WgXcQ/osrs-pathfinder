package dev.dqw4w9wgxcq.pathfinder.commons.domain.step;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

public record LinkStep(Type type, Link link) implements Step {
    public LinkStep(Link link) {
        this(Type.LINK, link);
    }
}
