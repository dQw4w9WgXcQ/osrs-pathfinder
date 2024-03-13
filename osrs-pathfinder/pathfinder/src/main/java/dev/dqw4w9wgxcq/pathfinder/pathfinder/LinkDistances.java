package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class LinkDistances {
    private final RemoteTilePathfinder remoteTilePathfinder;
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;

    public Map<Point, Integer> findDistances(Position position, boolean isOutbound) throws PathfinderException {
        var links = componentGraph.linksOfComponent(componentGrid.componentOf(position), isOutbound);
        if (links.isEmpty()) {
            log.debug(
                    "no links found in component {} {}",
                    componentGrid.componentOf(position),
                    isOutbound ? "outbound" : "inbound");
            return Map.of();
        }

        var ends = links.stream()
                .map(l -> isOutbound ? l.start() : l.end())
                .map(Position::toPoint)
                .collect(Collectors.toSet());

        var distances = remoteTilePathfinder.findDistances(position, ends);

        return distances.distances();
    }
}
