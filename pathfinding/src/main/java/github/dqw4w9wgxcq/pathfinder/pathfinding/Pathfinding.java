package github.dqw4w9wgxcq.pathfinder.pathfinding;

import github.dqw4w9wgxcq.pathfinder.domain.Agent;
import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.domain.pathstep.LinkStep;
import github.dqw4w9wgxcq.pathfinder.domain.pathstep.PathStep;
import github.dqw4w9wgxcq.pathfinder.domain.pathstep.WalkStep;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.pathfinding.linkdistances.LinkDistances;
import github.dqw4w9wgxcq.pathfinder.pathfinding.localpaths.LocalPaths;
import github.dqw4w9wgxcq.pathfinder.pathfinding.store.GraphStore;
import github.dqw4w9wgxcq.pathfinder.pathfinding.store.LinkStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class Pathfinding {
    public static void main(String[] args) throws IOException {
        var dir = new File(System.getProperty("user.dir"));
        var linkStore = LinkStore.load(new FileInputStream(new File(dir, "links.zip")));
        var graphStore = GraphStore.load(new FileInputStream(new File(dir, "graph.zip")), linkStore.links());

        var pathfinding = Pathfinding.create(graphStore);

        var path = pathfinding.findPath(
                new Position(3232, 3232, 0),
                new Position(2441, 3088, 0),
                new Agent(99, Collections.emptyMap(), Collections.emptyList())
        );

        if (path == null) {
            System.out.println("no path");
        } else {
            System.out.println(path);
        }
    }

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
        start = closestNotBlocked(componentGrid.planes(), start);
        finish = closestNotBlocked(componentGrid.planes(), finish);
        if (start == null || finish == null) {
            return new PathfindingResult(start, finish, null);
        }

        var linkPath = findLinkPath(start, finish, agent);

        if (linkPath == null) {
            log.info("no path from {} to {} for agent {}", start, finish, agent);
            return new PathfindingResult(start, finish, null);
        }

        var steps = toSteps(linkPath, start, finish);

        return new PathfindingResult(start, finish, steps);
    }

    private List<PathStep> toSteps(List<Link> linkPath, Position start, Position finish) {
        var startTime = System.currentTimeMillis();

        var curr = start;
        var steps = new ArrayList<PathStep>();
        for (var link : linkPath) {
            var walkPath = localPaths.get(curr.plane(), curr.point(), link.origin().point());

            steps.add(new WalkStep(curr.plane(), walkPath));
            steps.add(new LinkStep(link));

            curr = link.destination();
        }

        var walkPath = localPaths.get(curr.plane(), curr.point(), finish.point());
        steps.add(new WalkStep(curr.plane(), walkPath));

        var endTime = System.currentTimeMillis();
        log.info("steps in {}ms", endTime - startTime);

        return steps;
    }

    /**
     * Dijkstra-like algorithm.  need to simulate edges to the end point because can't modify the graph.
     */
    //todo pathfinding in reverse (better for teleports, multiple destinations instead of multiple origins)
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
                log.info("link path in {}ms", endTime - startTime);
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

        var endTime = System.currentTimeMillis();
        log.info("no link path in {}ms", endTime - startTime);
        return null;
    }

    public static @Nullable Position closestNotBlocked(int[][][] components, Position position) {
        var plane = components[position.plane()];
        if (plane[position.x()][position.y()] != -1) {
            return position;
        }

        var startTime = System.currentTimeMillis();
        log.debug("position {} is blocked, finding closest", position);

        var seen = new BitSet();
        var frontier = new ArrayDeque<Integer>();
        frontier.add(position.point().pack());
        var i = 0;
        while (!frontier.isEmpty()) {
            if (i++ > 10000) {
                log.info("no closest {}", position);
                return null;
            }

            var curr = frontier.poll();
            var currX = Point.unpackX(curr);
            var currY = Point.unpackY(curr);
            if (plane[currX][currY] != -1) {
                var finishTime = System.currentTimeMillis();
                log.info("closest position {} in {} from {}", Point.unpack(curr), finishTime - startTime, position);
                return new Position(currX, currY, position.plane());
            }

            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    var adjacentX = currX + dx;
                    var adjacentY = currY + dy;

                    if (adjacentX < 0 || adjacentX >= plane.length || adjacentY < 0 || adjacentY >= plane[0].length) {
                        continue;
                    }

//                    var adjacent = new Point(adjacentX, adjacentY);
                    var adjacent = Point.pack(adjacentX, adjacentY);
                    if (seen.get(adjacent)) {
                        continue;
                    }

                    seen.set(adjacent);
                    frontier.add(adjacent);
                }
            }
        }

        throw new IllegalStateException("No position found");
    }
}