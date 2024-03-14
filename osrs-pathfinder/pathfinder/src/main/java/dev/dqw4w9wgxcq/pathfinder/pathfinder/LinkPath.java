package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

import java.util.List;

public record LinkPath(int cost, List<Link> path) {}
