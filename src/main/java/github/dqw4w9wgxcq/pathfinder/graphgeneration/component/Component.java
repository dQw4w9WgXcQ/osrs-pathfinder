package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;

import java.util.List;

public record Component(int id, List<Link> outboundLinks, List<Link> inboundLinks) {
}
