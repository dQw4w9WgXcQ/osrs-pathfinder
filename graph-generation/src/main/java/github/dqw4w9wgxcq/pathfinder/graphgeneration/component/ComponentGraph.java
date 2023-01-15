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

@Slf4j
public class ComponentGraph {
    //todo traverse in reverse.  more efficient when using teleports.  multiple destinations instead of multiple origins.
    public static Map<Link, List<LinkEdge>> createGraph(LinkedComponents linkedComponents) {
        var graph = new HashMap<Link, List<LinkEdge>>();

        int count = 0;
        int skipCount = 0;
        for (var component : linkedComponents.components()) {
            for (var inboundLink : component.inboundLinks()) {
                for (var outboundLink : component.outboundLinks()) {
                    if (inboundLink == outboundLink) {
                        //can happen if the link origin and destination are in the same component
                        skipCount++;
                        continue;
                    }

                    //todo compute path for exact distance
                    int distance = chebychevDistance(inboundLink.destination(), outboundLink.origin());
                    var cost = outboundLink.cost() + distance;
                    var edge = new LinkEdge(outboundLink, cost);
                    log.debug("Adding edge {} to graph", edge);
                    graph.computeIfAbsent(inboundLink, k -> new ArrayList<>()).add(edge);
                    count++;
                }
            }
        }

        log.info(count + " edges");
        log.info(skipCount + " links skipped");

        return graph;
    }

    private static int chebychevDistance(Position p1, Position p2) {
        Preconditions.checkArgument(p1.getZ() == p2.getZ(), "p1({}) and p2({}) must be on the same plane", p1, p2);

        return Algo.chebyshevDistance(toPoint(p1), toPoint(p2));
    }

    private static Point toPoint(Position position) {
        return new Point(position.getX(), position.getY());
    }
}
