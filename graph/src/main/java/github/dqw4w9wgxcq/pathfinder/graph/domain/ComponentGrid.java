package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.Position;

public record ComponentGrid(int[][][] planes) {
    public int componentOf(Position position) {
        return planes[position.plane()][position.x()][position.y()];
    }
}
