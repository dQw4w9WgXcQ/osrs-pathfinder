package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.PathfinderResult;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.LinkStep;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.Step;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.WalkStep;
import dev.dqw4w9wgxcq.pathfinder.commons.store.GraphStore;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Slf4j
public class Pathfinder {
    private final ComponentGrid componentGrid;
    private final TilePathfinder tilePathfinder;

    private final ExecutorService asyncExe = Executors.newCachedThreadPool();
    private final LinkPathfinder linkPathfinder;

    Pathfinder(ComponentGrid componentGrid, ComponentGraph componentGraph, TilePathfinder tilePathfinder) {
        this.componentGrid = componentGrid;
        this.tilePathfinder = tilePathfinder;
        var linkDistances = new LinkDistances(tilePathfinder, componentGrid, componentGraph);
        this.linkPathfinder = new LinkPathfinder(componentGrid, componentGraph, linkDistances, asyncExe);
    }

    @SuppressWarnings("unused")
    public Pathfinder(GraphStore graphStore, TilePathfinder tilePathfinder) {
        this(new ComponentGrid(graphStore.componentGrid()), graphStore.componentGraph(), tilePathfinder);
    }

    @SuppressWarnings("unused")
    public PathfinderResult findPath(Position start, Position finish, Agent agent) {
        var startFuture = asyncExe.submit(() -> closestIfBlocked(start));
        var finishFuture = asyncExe.submit(() -> closestIfBlocked(finish));

        var fixedStart = Util.await(startFuture);
        var fixedFinish = Util.await(finishFuture);

        if (fixedStart == null || fixedFinish == null) {
            return new PathfinderResult.Blocked(fixedStart, fixedFinish);
        }

        var linkPath = linkPathfinder.findLinkPath(fixedStart, fixedFinish, agent);

        if (linkPath == null) {
            log.info("no path from {} to {} for agent {}", fixedStart, fixedFinish, agent);
            return new PathfinderResult.Unreachable(fixedStart, fixedFinish);
        }

        var steps = toSteps(linkPath, fixedStart, fixedFinish);

        return new PathfinderResult.Success(fixedStart, fixedFinish, steps);
    }

    /**
     * Finds tile paths between links and combines them into a list of path steps.
     */
    private List<Step> toSteps(List<Link> linkPath, Position start, Position finish) {
        var startTime = System.currentTimeMillis();

        var curr = start;
        var steps = new ArrayList<Supplier<Step>>();
        for (var link : linkPath) {
            var pathFuture =
                    findPathAsync(curr.plane(), curr.toPoint(), link.origin().toPoint());
            final var finalCurr = curr;
            steps.add(() -> awaitPath(finalCurr.plane(), pathFuture));
            steps.add(() -> new LinkStep(link));
            curr = link.destination();
        }

        var pathFuture = findPathAsync(curr.plane(), curr.toPoint(), finish.toPoint());
        final var finalCurr1 = curr;
        steps.add(() -> awaitPath(finalCurr1.plane(), pathFuture));

        var endTime = System.currentTimeMillis();
        log.debug("steps in {}ms", endTime - startTime);

        return steps.stream().map(Supplier::get).toList();
    }

    private Future<List<Point>> findPathAsync(int plane, Point start, Point end) {
        return asyncExe.submit(() -> tilePathfinder.findPath(plane, start, end));
    }

    private WalkStep awaitPath(int plane, Future<List<Point>> pathFuture) {
        return new WalkStep(plane, Util.await(pathFuture));
    }

    private @Nullable Position closestIfBlocked(Position position) {
        var plane = componentGrid.planes()[position.plane()];
        if (plane[position.x()][position.y()] != -1) {
            log.debug("position {} is not blocked", position);
            return position;
        }

        var startTime = System.currentTimeMillis();
        log.debug("position {} is blocked, finding closest", position);

        var seen = new HashSet<Point>();
        var frontier = new ArrayDeque<Point>();
        frontier.add(position.toPoint());
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
                        // no diagonals
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

        throw new IllegalStateException("No position found"); // will never happen unless the graph is invalid
    }
}
