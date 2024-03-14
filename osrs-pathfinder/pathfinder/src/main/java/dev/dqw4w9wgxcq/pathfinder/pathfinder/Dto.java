package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Dto {
    public record FindPathRequest(int plane, Point start, Point end, Algo algo) {}

    public record FindPathResponse(int size, int cost, ArrayList<Point> path) {}

    public record FindDistancesRequest(int plane, Point start, Set<Point> ends) {}

    public record FindDistancesResponse(List<Distance> distances) {}

    public record Distance(Point point, int distance) {}
}
