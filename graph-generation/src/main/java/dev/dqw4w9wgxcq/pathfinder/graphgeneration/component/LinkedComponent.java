package dev.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

import java.util.List;

public record LinkedComponent(List<Link> outboundLinks, List<Link> inboundLinks) {}
