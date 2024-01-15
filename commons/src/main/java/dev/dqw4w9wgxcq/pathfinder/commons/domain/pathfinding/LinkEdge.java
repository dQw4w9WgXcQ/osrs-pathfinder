package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

public record LinkEdge(Link link, int cost) implements Edge {}
