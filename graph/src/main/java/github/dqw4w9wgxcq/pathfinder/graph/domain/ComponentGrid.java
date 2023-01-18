package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.Position;

public record ComponentGrid(int[][][] grid) {
    public int componentOf(Position position) {
        return grid[position.plane()][position.x()][position.y()];
    }
}
