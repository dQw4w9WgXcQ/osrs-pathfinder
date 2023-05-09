package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.LinkStep;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.PathStep;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.WalkStep;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.linkdistances.LinkDistances;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.localpaths.LocalPaths;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.store.GraphStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class Pathfinding {
    private final ComponentGrid componentGrid;
    private final ComponentGraph componentGraph;
    private final LinkDistances linkDistances;
    private final LocalPaths localPaths;

    public static Pathfinding create(GraphStore graphStore) {
        var pathfindingWorld = PathfindingWorld.create(graphStore.grid());
        var componentGrid = new ComponentGrid(graphStore.componentGrid());
        var linkDistances = new LinkDistances(pathfindingWorld, componentGrid, graphStore.componentGraph());
        var localPaths = new LocalPaths(pathfindingWorld);
        return new Pathfinding(componentGrid, graphStore.componentGraph(), linkDistances, localPaths);
    }

    public PathfindingResult findPath(Position start, Position finish, Agent agent) {
        start = closestIfBlocked(start);
        finish = closestIfBlocked(finish);
        if (start == null || finish == null) {
            return new PathfindingResult(start, finish, null);
        }

        var linkPath = findLinkPath(start, finish, agent);

        if (linkPath == null) {
            log.debug("no path from {} to {} for agent {}", start, finish, agent);
            return new PathfindingResult(start, finish, null);
        }

        var steps = toPathSteps(linkPath, start, finish);

        return new PathfindingResult(start, finish, steps);
    }

    /**
     * Finds tile paths between links and combines them into a list of path steps.
     */
    private List<PathStep> toPathSteps(List<Link> linkPath, Position start, Position finish) {
        var startTime = System.currentTimeMillis();

        var curr = start;
        var steps = new ArrayList<PathStep>();
        for (var link : linkPath) {
            var walkPath = localPaths.get(curr.plane(), curr.point(), link.origin().point());

            steps.add(new WalkStep(curr.plane(), walkPath));
            steps.add(new LinkStep(link));

            curr = link.destination();
        }

        var tilePath = localPaths.get(curr.plane(), curr.point(), finish.point());
        steps.add(new WalkStep(curr.plane(), tilePath));

        var endTime = System.currentTimeMillis();
        log.debug("steps in {}ms", endTime - startTime);

        return steps;
    }

    /**
     * Dijkstra-like algorithm.  Edges to the end component are simulated to avoid modifying the graph.  (see comment below)
     */
    private @Nullable List<Link> findLinkPath(Position start, Position finish, Agent agent) {
        record Node(Link link, int distance, boolean isEnd) {
        }

        log.debug("link path from {} to {} for agent {}", start, finish, agent);

        var startTime = System.currentTimeMillis();

        var startComponent = componentGrid.componentOf(start);
        var endComponent = componentGrid.componentOf(finish);

        var startDistances = linkDistances.get(start, true);
        var endDistances = linkDistances.get(finish, false);

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

                var endTime = System.currentTimeMillis();
                log.debug("link path (took {}ms)", endTime - startTime);
                return path;
            }

            //simulate end component edges
            if (componentGrid.componentOf(curr.link().destination()) == endComponent) {
                log.debug("adding end link {}", curr);
                queue.add(new Node(curr.link(), curr.distance() + endDistances.get(curr.link().destination().point()), true));
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

    private @Nullable Position closestIfBlocked(Position position) {
        var plane = componentGrid.planes()[position.plane()];
        if (plane[position.x()][position.y()] != -1) {
            return position;
        }

        var startTime = System.currentTimeMillis();
        log.debug("position {} is blocked, finding closest", position);

        var seen = new HashSet<Point>();
        var frontier = new ArrayDeque<Point>();
        frontier.add(position.point());
        var i = 0;
        while (!frontier.isEmpty()) {
            if (i++ > 10000) {
                log.info("no closest {}", position);
                return null;
            }

            var curr = frontier.poll();
            if (plane[curr.x()][curr.y()] != -1) {
                var finishTime = System.currentTimeMillis();
                log.debug("closest position {} in {} from {}", curr, finishTime - startTime, position);
                return new Position(curr.x(), curr.y(), position.plane());
            }

            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    if (dy != 0 && dx != 0) {
                        //no diagonals
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

        throw new IllegalStateException("No position found");//will never happen unless the graph is invalid
    }
}