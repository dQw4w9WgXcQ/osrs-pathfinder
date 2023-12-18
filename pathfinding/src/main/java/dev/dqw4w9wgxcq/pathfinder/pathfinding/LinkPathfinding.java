package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.tile.LinkDistances;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
class LinkPathfinding {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;
    private final LinkDistances linkDistances;
    private final ExecutorService asyncExe;

    /**
     * Dijkstra-like algorithm.  Edges to the end component are simulated to avoid modifying the graph.  (see comment below)
     */
    @Nullable List<Link> findLinkPath(Position start, Position finish, Agent agent) {
        record Node(Link link, int distance, boolean isEnd) {
        }

        log.debug("link path from {} to {} for agent {}", start, finish, agent);

        var startTime = System.currentTimeMillis();

        var startComponent = componentGrid.componentOf(start);
        var endComponent = componentGrid.componentOf(finish);

        var startDistancesFuture = asyncExe.submit(() -> linkDistances.findDistances(start, true));
        var endDistancesFuture = asyncExe.submit(() -> linkDistances.findDistances(finish, false));

        var startDistances = Util.await(startDistancesFuture);
        var endDistances = Util.await(endDistancesFuture);

        var seenFrom = new HashMap<Link, Link>();
        var linkDistances = new HashMap<Link, Integer>();
        var queue = new PriorityQueue<>(Comparator.comparingInt(Node::distance));

        var startLinks = componentGraph.linksOfComponent(startComponent, true);
        if (startLinks.isEmpty()) {
            log.debug("no links in start component {}", startComponent);

            if (startComponent == endComponent) {
                return List.of();
            }

            return null;
        }

        log.debug("{} start links", startLinks.size());
        for (var startLink : startLinks) {
            if (agent.hasRequirements(startLink.requirements())) {
                var distance = startDistances.get(startLink.origin().toPoint());
                queue.add(new Node(startLink, distance, false));
                linkDistances.put(startLink, distance);
            }
        }

        while (!queue.isEmpty()) {
            var curr = queue.poll();
            log.debug("curr: {}", curr);

            if (curr.isEnd()) {
                log.debug("found end {}", curr);
                var path = new ArrayList<Link>();
                var link = curr.link();
                while (link != null) {
                    path.add(link);
                    link = seenFrom.get(link);
                }
                Collections.reverse(path);

                var endTime = System.currentTimeMillis();
                log.debug("link path took {}ms", endTime - startTime);
                return path;
            }

            //simulate end component edges
            if (componentGrid.componentOf(curr.link().destination()) == endComponent) {
                log.debug("adding end link {}", curr);
                queue.add(new Node(curr.link(), curr.distance() + endDistances.get(curr.link().destination().toPoint()), true));
                //don't need linkDistances because finding an end node terminates the search
            }

            var linkEdges = componentGraph.graph().get(curr.link());
            if (linkEdges == null) {
                log.debug("no edges for link {}", curr.link());
                continue;
            }

            for (var linkEdge : linkEdges) {
                var nextLink = linkEdge.link();

                if (!agent.hasRequirements(nextLink.requirements())) {
                    log.debug("requirements not met for link{}", nextLink);
                    continue;
                }

                var nextDistance = curr.distance() + linkEdge.cost();

                var linkDistance = linkDistances.getOrDefault(nextLink, Integer.MAX_VALUE);
                if (nextDistance >= linkDistance) {
                    continue;
                }

                log.debug("adding link {}", nextLink);
                queue.add(new Node(nextLink, nextDistance, false));
                linkDistances.put(nextLink, nextDistance);
                seenFrom.put(nextLink, curr.link());
            }
        }

        var endTime = System.currentTimeMillis();
        log.debug("no link path (took {}ms)", endTime - startTime);
        return null;
    }
}
