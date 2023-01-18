package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.domain.*;
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

    public @Nullable List<PathStep> findPath(Position start, Position end, Agent agent) {
        start = findClosestNotBlocked(start);
        end = findClosestNotBlocked(end);

        var linkPath = findLinkPath(start, end, agent);
        if (linkPath == null) {
            log.debug("no path from {} to {} for agent {}", start, end, agent);
            return null;
        }

        return toSteps(linkPath, start, end);
    }

    private List<PathStep> toSteps(List<Link> linkPath, Position start, Position end) {
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

        return steps;
    }

    private @Nullable List<Link> findLinkPath(Position start, Position end, Agent agent) {
        int startComponent = componentGrid.componentOf(start);
        int endComponent = componentGrid.componentOf(end);

        var startDistances = linkDistances.get(start);
        var endDistances = linkDistances.get(end);

        record Node(Link link, int distance, boolean isEnd) {
        }
        var seenFrom = new HashMap<Link, Link>();
        var queue = new PriorityQueue<>(Comparator.comparingInt(Node::distance));

        for (var startLink : componentGraph.linksOf(startComponent)) {
            if (agent.hasRequirements(startLink.requirements())) {
                var distance = startDistances.get(startLink.origin());
                queue.add(new Node(startLink, distance, false));
            }
        }

        while (!queue.isEmpty()) {
            var curr = queue.poll();
            if (curr.isEnd()) {
                var path = new ArrayList<Link>();
                var link = curr.link();
                while (link != null) {
                    path.add(0, link);
                    link = seenFrom.get(link);
                }
                return path;
            }

            //simulate end component edges
            if (componentGrid.componentOf(curr.link().destination()) == endComponent) {
                queue.add(new Node(curr.link(), curr.distance() + endDistances.get(curr.link().destination()), true));
            }

            for (var edge : componentGraph.graph().get(curr.link())) {
                var nextLink = edge.link();
                if (seenFrom.containsKey(nextLink)) {
                    continue;
                }

                seenFrom.putIfAbsent(nextLink, curr.link());
                queue.add(new Node(nextLink, curr.distance() + edge.cost(), false));
            }
        }

        return null;
    }

    private Position findClosestNotBlocked(Position position) {
        int[][] plane = componentGrid.planes()[position.plane()];
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
                return new Position(position.plane(), curr.x(), curr.y());
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
