package github.dqw4w9wgxcq.pathfinder.graph.linkdistances;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class LinkDistanceCache {
    private record CacheKey(Position position, boolean outbound) {
    }

    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    //todo eviction
    private final Map<CacheKey, Map<Point, Integer>> cache = new ConcurrentHashMap<>();

    public Map<Point, Integer> get(Position position, boolean outbound) {
        return cache.computeIfAbsent(new CacheKey(position, outbound), this::compute);
    }

    private Map<Point, Integer> compute(CacheKey key) {
        log.debug("cache miss for {}", key);

        var links = componentGraph.linksOfComponent(componentGrid.componentOf(key.position()), key.outbound());
        var tos = links.stream()
                .map(l -> key.outbound() ? l.origin() : l.destination())
                .map(Position::point)
                .collect(Collectors.toSet());
        return Algo.distances(componentGrid.planes()[key.position().plane()], key.position().point(), tos);
    }
}