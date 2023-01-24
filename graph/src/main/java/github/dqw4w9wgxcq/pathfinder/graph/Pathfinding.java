package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.domain.*;
import github.dqw4w9wgxcq.pathfinder.graph.edge.LinkEdge;
import github.dqw4w9wgxcq.pathfinder.graph.linkdistances.LinkDistanceCache;
import github.dqw4w9wgxcq.pathfinder.graph.localpaths.LocalPathCache;
import github.dqw4w9wgxcq.pathfinder.graph.store.GraphStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class Pathfinding {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;
    private final LinkDistanceCache linkDistances;
    private final LocalPathCache localPaths;

    public static Pathfinding create(GraphStore graphStore) {
        var componentGrid = new ComponentGrid(graphStore.planes());
        var linkDistances = new LinkDistanceCache(componentGrid, graphStore.componentGraph());
        var localPaths = new LocalPathCache(componentGrid);
        return new Pathfinding(componentGrid, graphStore.componentGraph(), linkDistances, localPaths);
    }

    public @Nullable List<PathStep> findPath(Position start, Position end, Agent agent) {
        start = findClosestNotBlocked(componentGrid.planes(), start);
        end = findClosestNotBlocked(componentGrid.planes(), end);

        var startTime = System.currentTimeMillis();
        var linkPath = findLinkPath(start, end, agent);
        var endTime = System.currentTimeMillis();
        log.info("found link path in {}ms", endTime - startTime);
        if (linkPath == null) {
            log.debug("no path from {} to {} for agent {}", start, end, agent);
            return null;
        }

        return toSteps(linkPath, start, end);
    }

    private List<PathStep> toSteps(List<Link> linkPath, Position start, Position end) {
        long startTime = System.currentTimeMillis();
        var curr = start;
        var steps = new ArrayList<PathStep>();
        for (var link : linkPath) {
            var walkPath = localPaths.get(curr.plane(), curr.point(), link.origin().point());

            steps.add(new WalkStep(curr.plane(), walkPath));
            steps.add(new LinkStep(link));

            curr = link.destination();
        }

        var walkPath = localPaths.get(curr.plane(), curr.point(), end.point());
        steps.add(new WalkStep(curr.plane(), walkPath));

        long endTime = System.currentTimeMillis();
        log.info("converted link path to steps in {}ms", endTime - startTime);
        return steps;
    }

    /**
     * Dijkstra-like algorithm.  need to simulate edges to the end point because can't modify the graph.
     */
    //todo pathfinding in reverse (better for teleports, multiple destinations instead of multiple origins)
    private @Nullable List<Link> findLinkPath(Position start, Position end, Agent agent) {
        record Node(Link link, int distance, boolean isEnd) {
        }

        log.debug("find link path from {} to {} for agent {}", start, end, agent);

        int startComponent = componentGrid.componentOf(start);
        int endComponent = componentGrid.componentOf(end);

        var startDistances = linkDistances.get(start, true);
        var endDistances = linkDistances.get(end, false);

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
                var distance = startDistances.get(startLink.origin().point());
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
                return path;
            }

            //simulate end component edges
            if (componentGrid.componentOf(curr.link().destination()) == endComponent) {
                log.debug("adding end link {}", curr);
                queue.add(new Node(curr.link(), curr.distance() + endDistances.get(curr.link().destination().point()), true));
                //don't need linkDistances because finding an end node terminates the search
            }

            List<LinkEdge> linkEdges = componentGraph.graph().get(curr.link());
            if (linkEdges == null) {
                log.debug("no edges for link {}", curr.link());
                continue;
            }

            for (var linkEdge : linkEdges) {
                var nextLink = linkEdge.link();
                var nextDistance = curr.distance() + linkEdge.cost();
                var linkDistance = linkDistances.getOrDefault(nextLink, Integer.MAX_VALUE);
                if (nextDistance < linkDistance) {
                    log.debug("adding link {}", nextLink);
                    queue.add(new Node(nextLink, nextDistance, false));
                    linkDistances.put(nextLink, nextDistance);
                    seenFrom.put(nextLink, curr.link());
                }
            }
        }

        return null;
    }

    public static Position findClosestNotBlocked(int[][][] grid, Position position) {
        int[][] plane = grid[position.plane()];
        if (plane[position.x()][position.y()] != -1) {
            return position;
        }

        log.debug("position {} is blocked, finding closest", position);

        var seen = new HashSet<Point>();
        var frontier = new ArrayList<Point>();
        frontier.add(position.point());
        while (!frontier.isEmpty()) {
            var curr = frontier.remove(0);
            if (plane[curr.x()][curr.y()] != -1) {
                log.debug("found closest position {}", curr);
                return new Position(curr.x(), curr.y(), position.plane());
            }

            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    var adjacentX = curr.x() + dx;
                    var adjacentY = curr.y() + dy;

                    if (adjacentX < 0 || adjacentX >= plane.length || adjacentY < 0 || adjacentY >= plane[0].length) {
                        continue;
                    }

                    var adjacent = new Point(adjacentX, adjacentY);
                    if (seen.contains(adjacent)) {
                        continue;
                    }

                    seen.add(adjacent);
                    frontier.add(adjacent);
                }
            }
        }

        throw new IllegalStateException("No position in a component found");
    }
}