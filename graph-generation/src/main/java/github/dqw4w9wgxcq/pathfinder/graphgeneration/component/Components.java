package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graph.edge.LinkEdge;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Components {
    private static final boolean ESTIMATE_DISTANCES = false;//to speed up graph generation during development

    public static ComponentGraph createGraph(LinkedComponents linkedComponents, ContiguousComponents contiguousComponents) {
        log.info("Creating component graph");

        var graph = new HashMap<Link, List<LinkEdge>>();

        int count = 0;
        int skipCount = 0;
        for (var component : linkedComponents.linkedComponents()) {
            for (var inboundLink : component.inboundLinks()) {
                Map<Point, Integer> distances;
                if (!ESTIMATE_DISTANCES) {
                    distances = Algo.distances(
                            contiguousComponents.planes()[inboundLink.destination().plane()],
                            inboundLink.destination().point(),
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
                    log.debug("Adding edge {} to graph {}", edge, count);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                    count++;
                }
            }
        }

        var componentLinks = new ArrayList<List<Link>>();
        for (var component : linkedComponents.linkedComponents()) {
            componentLinks.add(component.outboundLinks());
        }

        log.info(count + " edges " + skipCount + " links skipped");
        return new ComponentGraph(graph, componentLinks);
    }

    public static ComponentGrid createGrid(ContiguousComponents contiguousComponents) {
        return new ComponentGrid(contiguousComponents.planes());
    }
}
