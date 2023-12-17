package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

public record LinkStep(Link.Type type, Link link) implements PathStep {
    public LinkStep(Link link) {
        this(link.type(), link);
    }
}
