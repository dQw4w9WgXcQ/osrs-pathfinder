package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MinifyPath {
    public static List<Point> toMinifiedPath(List<Point> path) {
        if (path.isEmpty()) return List.of();

        var minified = new ArrayList<Point>();
        Point prevPrev = null;
        Point prev = null;
        for (var curr : path) {
            if (prev == null) {
                prev = curr;
                minified.add(curr);
                continue;
            }

            if (prevPrev == null) {
                prevPrev = prev;
                prev = curr;
                continue;
            }

            var dx = prev.x() - prevPrev.x();
            var dy = prev.y() - prevPrev.y();
            var dx2 = curr.x() - prev.x();
            var dy2 = curr.y() - prev.y();

            if (dx != dx2 || dy != dy2) {
                minified.add(prev);
            }

            prevPrev = prev;
            prev = curr;
        }

        minified.add(prev);
        return minified;
    }
}
