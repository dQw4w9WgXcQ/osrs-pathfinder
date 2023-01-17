package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.edge.LinkEdge;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ComponentGraph {
    //todo traverse in reverse.  more efficient when using teleports.  multiple destinations instead of multiple origins.
    public static Map<Link, List<LinkEdge>> createGraph(LinkedComponents linkedComponents, ContiguousComponents contiguousComponents) {
        log.info("Creating component graph");

        var graph = new HashMap<Link, List<LinkEdge>>();

        int count = 0;
        int skipCount = 0;
        for (var component : linkedComponents.linkedComponents()) {
            for (var inboundLink : component.inboundLinks()) {
                var distances = Algo.distances(contiguousComponents.planes()[inboundLink.destination().plane()], inboundLink.destination().toPoint());
                log.info("distances size: {}", distances.size());
                for (var outboundLink : component.outboundLinks()) {
                    if (inboundLink == outboundLink) {
                        //can happen if the link origin and destination are in the same component
                        skipCount++;
                        continue;
                    }

                    log.info("Adding edge from {} to {}", inboundLink, outboundLink);
                    var cost = outboundLink.cost() + distances.get(outboundLink.origin().toPoint());
                    var edge = new LinkEdge(outboundLink, cost);
                    log.info("Adding edge {} to graph {}", edge, count);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                    count++;
                }
            }
        }

        log.info(count + " edges " + skipCount + " links skipped");
        return graph;
    }
}
