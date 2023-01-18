package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class LinkDistanceCache {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    //todo eviction
    private final Map<Position, Map<Point, Integer>> cache = new HashMap<>();

    public Map<Point, Integer> get(Position position) {
        return cache.computeIfAbsent(position, this::compute);
    }

    private Map<Point, Integer> compute(Position position) {
        log.debug("cache miss for {}", position);

        var links = componentGraph.linksOf(componentGrid.componentOf(position));
        Set<Point> tos = links.stream()
                .map(Link::origin)
                .map(Position::point)
                .collect(Collectors.toSet());
        return Algo.distances(componentGrid.grid()[position.plane()], position.point(), tos);
    }
}
