package github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;

import java.util.Map;

public record Component(int id, Map<Point, ComponentEdge> edges) {
}
