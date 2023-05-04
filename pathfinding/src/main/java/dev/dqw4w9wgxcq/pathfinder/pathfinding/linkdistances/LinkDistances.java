package dev.dqw4w9wgxcq.pathfinder.pathfinding.linkdistances;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingWorld;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class LinkDistances {
    private record CacheKey(Position position, boolean outbound) {
    }

    private final PathfindingWorld pathfindingWorld;
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    //todo eviction
    private final Map<CacheKey, Map<Point, Integer>> cache = new ConcurrentHashMap<>();

    public Map<Point, Integer> get(Position position, boolean outbound) {
        return cache.computeIfAbsent(new CacheKey(position, outbound), this::compute);
    }

    private Map<Point, Integer> compute(CacheKey key) {
        log.debug("cache miss for {} {}", key.position(), key.outbound() ? "outbound" : "inbound");

        var links = componentGraph.linksOfComponent(componentGrid.componentOf(key.position()), key.outbound());
        var tos = links.stream()
                .map(l -> key.outbound() ? l.origin() : l.destination())
                .map(Position::point)
                .collect(Collectors.toSet());

        var startTime = System.currentTimeMillis();
        var distances = pathfindingWorld.distances(key.position(), tos);
        var finishTime = System.currentTimeMillis();
        log.info("distances {} in {}ms", key.outbound() ? "outbound" : "inbound", finishTime - startTime);

        return distances;
    }
}