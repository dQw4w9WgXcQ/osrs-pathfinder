package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.domain.LinkEdge;
import github.dqw4w9wgxcq.pathfinder.graph.domain.LinkRef;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ComponentGraph {
    //todo traverse in reverse.  more efficient when using teleports.  multiple destinations instead of multiple origins.
    public static Map<LinkRef, List<LinkEdge>> createGraph(LinkedComponents linkedComponents, ContiguousComponents contiguousComponents) {
        log.info("Creating component graph");

        var graph = new HashMap<LinkRef, List<LinkEdge>>();

        int count = 0;
        int skipCount = 0;
        for (var component : linkedComponents.linkedComponents()) {
            for (var inboundLink : component.inboundLinks()) {
                for (var outboundLink : component.outboundLinks()) {
                    if (inboundLink == outboundLink) {
                        //can happen if the link origin and destination are in the same component
                        skipCount++;
                        continue;
                    }

                    //todo parallelize (graph takes 30min to generate atm)
                    int distance = distance(contiguousComponents.planes()[inboundLink.destination().plane()], inboundLink.destination(), outboundLink.origin());
                    var cost = outboundLink.cost() + distance;
                    var edge = new LinkEdge(LinkRef.from(outboundLink), cost);
                    log.info("Adding edge {} to graph {}", edge, count);
                    graph.computeIfAbsent(LinkRef.from(inboundLink), k -> new ArrayList<>()).add(edge);
                    count++;
                }
            }
        }

        log.info(count + " edges " + skipCount + " links skipped");
        return graph;
    }

    private static int distance(int[][] grid, Position p1, Position p2) {
        Preconditions.checkArgument(p1.plane() == p2.plane(), "p1({}) and p2({}) must be on the same plane", p1, p2);

        if (true) return Algo.chebyshevDistance(p1.toPoint(), p2.toPoint());//todo

        return Objects.requireNonNull(Algo.bfs(grid, p1.toPoint(), p2.toPoint())).size();
    }
}
