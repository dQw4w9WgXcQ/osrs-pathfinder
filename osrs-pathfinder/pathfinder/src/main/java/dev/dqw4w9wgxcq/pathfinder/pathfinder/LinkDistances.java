package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class LinkDistances {
    private final TilePathfinder tilePathfinder;
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    private record CacheKey(Position position, boolean isOutbound) {}

    private final Map<CacheKey, Map<Point, Integer>> cache = new ConcurrentHashMap<>();

    public Map<Point, Integer> findDistances(Position position, boolean isOutbound) {
        //        if (!tilePathfinder.isRemote()) {
        //            return cache.computeIfAbsent(new CacheKey(position, isOutbound), k -> {
        //                log.debug("cache miss {} {}", k.position(), k.isOutbound() ? "outbound" : "inbound");
        //                return internalFindDistances(k.position(), k.isOutbound());
        //            });
        //        }

        return internalFindDistances(position, isOutbound);
    }

    private Map<Point, Integer> internalFindDistances(Position position, boolean isOutbound) {
        var links = componentGraph.linksOfComponent(componentGrid.componentOf(position), isOutbound);
        if (links.isEmpty()) {
            log.debug(
                    "no links found in component {} {}",
                    componentGrid.componentOf(position),
                    isOutbound ? "outbound" : "inbound");
            return Map.of();
        }

        var tos = links.stream()
                .map(l -> isOutbound ? l.origin() : l.destination())
                .map(Position::toPoint)
                .collect(Collectors.toSet());

        var startTime = System.currentTimeMillis();
        var distances = tilePathfinder.distances(position, tos);
        var finishTime = System.currentTimeMillis();
        log.debug("distances {} in {}ms", isOutbound ? "outbound" : "inbound", finishTime - startTime);

        return distances;
    }
}
