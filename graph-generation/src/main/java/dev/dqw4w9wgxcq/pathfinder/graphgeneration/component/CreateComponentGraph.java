package dev.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.Algo;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingWorld;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.edge.LinkEdge;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CreateComponentGraph {
    private static final boolean ESTIMATE_DISTANCES = false;//to speed up graph generation during development

    public static ComponentGraph create(LinkedComponents linkedComponents, PathfindingWorld pathfindingWorld) {
        log.info("Creating component graph, count:{}", linkedComponents.linkedComponents().size());

        var graph = new HashMap<Link, List<LinkEdge>>();

        var count = 0;
        var skipCount = 0;
        for (var component : linkedComponents.linkedComponents()) {
            var startTime = System.currentTimeMillis();
            for (var inboundLink : component.inboundLinks()) {
                Map<Point, Integer> distances;
                if (!ESTIMATE_DISTANCES) {
                    distances = pathfindingWorld.distances(
                            inboundLink.destination(),
                            component.outboundLinks().stream()
                                    .map(Link::origin)
                                    .map(Position::point)
                                    .collect(Collectors.toSet())
                    );
                }

                for (var outboundLink : component.outboundLinks()) {
                    if (inboundLink == outboundLink) {
                        //can happen if the link origin and destination are in the same component
                        skipCount++;
                        continue;
                    }

                    log.debug("Adding edge from {} to {}", inboundLink, outboundLink);
                    var distance = ESTIMATE_DISTANCES
                            ? Algo.chebyshev(inboundLink.destination().point(), outboundLink.origin().point())
                            : distances.get(outboundLink.origin().point());
                    var cost = outboundLink.cost() + distance;

                    var edge = new LinkEdge(outboundLink, cost);
                    log.debug("Adding edge {} to graph", edge);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                }
            }

            var time = System.currentTimeMillis() - startTime;
            if (time > 10 * 1000) {
                log.info("component {} took {} s", count, (time) / 1000);
            }
            count++;
        }

        var outboundLinks = new ArrayList<List<Link>>();
        var inboundLinks = new ArrayList<List<Link>>();
        for (var component : linkedComponents.linkedComponents()) {
            outboundLinks.add(component.outboundLinks());
            inboundLinks.add(component.inboundLinks());
        }

        log.info(count + " edges " + skipCount + " links skipped");
        return new ComponentGraph(graph, outboundLinks, inboundLinks);
    }
}
