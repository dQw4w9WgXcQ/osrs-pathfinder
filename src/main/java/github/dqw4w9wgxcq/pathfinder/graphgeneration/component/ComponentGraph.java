package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Algo;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo traverse in reverse.  more efficient when using teleports.  multiple destinations instead of multiple origins.
@Slf4j
public record ComponentGraph(Map<Link, List<LinkEdge>> graph) {
    public static ComponentGraph create(LinkedComponents linkedComponents) {
        var graph = new HashMap<Link, List<LinkEdge>>();

        int count = 0;
        int sameComponentCount = 0;
        for (var component : linkedComponents.components()) {
            for (var inboundLink : component.inboundLinks()) {
                for (var outboundLink : component.outboundLinks()) {
                    //can happen if the link origin and destination are in the same component
                    if (inboundLink == outboundLink) {
                        sameComponentCount++;
                        continue;
                    }

                    int distance = chebychevDistance(inboundLink.destination(), outboundLink.origin());
                    var cost = outboundLink.cost() + distance;
                    var edge = new LinkEdge(outboundLink, cost);
                    log.info("Adding edge {} to graph", edge);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                    count++;
                }
            }
        }

        for (var linkListEntry : graph.entrySet()) {
            var link = linkListEntry.getKey();
            var adjacent = linkListEntry.getValue();
            log.info("Link {} has {} adjacent links", link, adjacent.size());
            for (LinkEdge linkEdge : adjacent) {
                log.info("adjacent {}", linkEdge);
            }
        }

        log.info(count + " edges in graph");
        log.info(sameComponentCount + " links skipped");

        return new ComponentGraph(graph);
    }

    private static int chebychevDistance(Position p1, Position p2) {
        Preconditions.checkArgument(p1.getZ() == p2.getZ(), "p1({}) and p2({}) must be on the same plane", p1, p2);

        return Algo.chebyshevDistance(toPoint(p1), toPoint(p2));
    }

    private static Point toPoint(Position position) {
        return new Point(position.getX(), position.getY());
    }
}
