package dev.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.LinkEdge;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.LocalTilePathfinder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CreateComponentGraph {
    public static ComponentGraph create(LinkedComponents linkedComponents, LocalTilePathfinder tilePathfinder) {
        log.info(
                "Creating component graph, linkedComponents size:{}",
                linkedComponents.linkedComponents().size());

        var graph = new HashMap<Link, List<LinkEdge>>();

        var count = 0;
        var skippedCount = 0;
        var components = linkedComponents.linkedComponents();
        for (var component : components) {
            if (count % 10_000 == 0 && count != 0) {
                log.info("component {}...", count);
            }

            var startTime = System.currentTimeMillis();
            for (var inboundLink : component.inboundLinks()) {
                var distances = tilePathfinder.distances(
                        inboundLink.end(),
                        component.outboundLinks().stream()
                                .map(Link::start)
                                .map(Position::toPoint)
                                .collect(Collectors.toSet()));

                for (var outboundLink : component.outboundLinks()) {
                    if (inboundLink == outboundLink) {
                        // can happen if the link start and end are in the same component
                        skippedCount++;
                        continue;
                    }

                    log.debug("Adding edge from {} to {}", inboundLink, outboundLink);
                    var distance = distances.get(outboundLink.start().toPoint());
                    var cost = outboundLink.cost() + distance;

                    var edge = new LinkEdge(outboundLink, cost);
                    log.debug("Adding edge {} to graph", edge);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                }
            }

            var time = System.currentTimeMillis() - startTime;
            if (time > 10_000) {
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

        log.info(count + " edges " + skippedCount + " links skipped");
        return new ComponentGraph(graph, outboundLinks, inboundLinks);
    }
}
