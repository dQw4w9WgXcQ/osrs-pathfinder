package github.dqw4w9wgxcq.pathfinder.graph.linkdistances;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.Components;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class LinkDistanceCache {
    private final Components components;

    public Map<Point, Integer> get(Position position) {
        //todo
        return Algo.distances(components.grid()[position.plane()], position.toPoint());
    }
}
