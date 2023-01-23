package github.dqw4w9wgxcq.pathfinder.graph.linkdistances;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class LinkDistanceCache {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    //todo eviction
    private final Map<CacheKey, Map<Point, Integer>> cache = new ConcurrentHashMap<>();

    public Map<Point, Integer> get(Position position, boolean origins) {
        return cache.computeIfAbsent(new CacheKey(position, origins), this::compute);
    }

    private Map<Point, Integer> compute(CacheKey key) {
        log.debug("cache miss for {}", key);

        var links = componentGraph.linksOfComponent(componentGrid.componentOf(key.position()));
        Set<Point> tos = links.stream()
                .map(l -> key.origins() ? l.origin() : l.destination())
                .map(Position::point)
                .collect(Collectors.toSet());
        return Algo.distances(componentGrid.planes()[key.position().plane()], key.position().point(), tos);
    }

    private record CacheKey(Position position, boolean origins) {
    }
}
