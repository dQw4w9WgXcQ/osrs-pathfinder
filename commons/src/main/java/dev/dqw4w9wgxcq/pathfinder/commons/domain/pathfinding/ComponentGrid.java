package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;

public record ComponentGrid(int[][][] planes) {
    public int componentOf(Position position) {
        var componentId = planes[position.plane()][position.x()][position.y()];
        if (componentId < -1) throw new IllegalStateException("Component id is < -1 " + componentId);
        return componentId;
    }

    public boolean isBlocked(Position position) {
        return isBlocked(planes[position.plane()], position.x(), position.y());
    }

    public static boolean isBlocked(int[][] grid, int x, int y) {
        return grid[x][y] < 0;
    }
}
