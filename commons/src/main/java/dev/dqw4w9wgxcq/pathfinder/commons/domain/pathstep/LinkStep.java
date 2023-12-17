package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.LinkType;

public record LinkStep(LinkType type, Link link) implements PathStep {
    public LinkStep(Link link) {
        this(link.type(), link);
    }
}
