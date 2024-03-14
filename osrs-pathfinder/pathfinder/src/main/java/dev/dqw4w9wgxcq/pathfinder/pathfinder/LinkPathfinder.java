package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
class LinkPathfinder {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;
    private final LinkDistances linkDistances;
    private final ExecutorService asyncExe;

    /**
     * Dijkstra-like algorithm.  Edges to the end component are simulated to avoid modifying the graph.  (see comment below)
     */
    public @Nullable LinkPath findLinkPath(Position start, Position end, Agent agent) {
        record Node(Link link, int distance, boolean isEnd) {}

        log.debug("link path from {} to {} for agent {}", start, end, agent);

        var startTime = System.currentTimeMillis();

        var startComponent = componentGrid.componentOf(start);
        var endComponent = componentGrid.componentOf(end);

        var startDistancesFuture = asyncExe.submit(() -> linkDistances.findDistances(start, true));
        var endDistancesFuture = asyncExe.submit(() -> linkDistances.findDistances(end, false));

        var startDistances = Util.await(startDistancesFuture);
        var endDistances = Util.await(endDistancesFuture);

        var seenFrom = new HashMap<Link, Link>();
        var linkDistances = new HashMap<Link, Integer>();
        var queue = new PriorityQueue<>(Comparator.comparingInt(Node::distance));

        var startLinks = componentGraph.linksOfComponent(startComponent, true);
        if (startLinks.isEmpty()) {
            log.debug("no links in start component {}", startComponent);

            if (startComponent == endComponent) {
                return new LinkPath(0, List.of());
            }

            return null;
        }

        log.debug("{} start links", startLinks.size());
        for (var startLink : startLinks) {
            if (agent.checkRequirements(startLink.requirements())) {
                var distance = startDistances.get(startLink.start().toPoint());
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
                return new LinkPath(linkDistances.get(curr.link()) + curr.distance(), path);
            }

            // simulate end component edges
            if (componentGrid.componentOf(curr.link().end()) == endComponent) {
                log.debug("adding end link {}", curr);
                queue.add(new Node(
                        curr.link(),
                        curr.distance() + endDistances.get(curr.link().end().toPoint()),
                        true));
                // don't need linkDistances because finding an end node terminates the search
            }

            var linkEdges = componentGraph.graph().get(curr.link());
            if (linkEdges == null) {
                log.debug("no edges for link {}", curr.link());
                continue;
            }

            for (var linkEdge : linkEdges) {
                var nextLink = linkEdge.link();

                if (!agent.checkRequirements(nextLink.requirements())) {
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
