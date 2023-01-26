package github.dqw4w9wgxcq.pathfinder.pathfinding.domain;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;

public record ComponentGrid(int[][][] planes) {
    public int componentOf(Position position) {
        return planes[position.plane()][position.x()][position.y()];
    }

    public boolean isBlocked(Position position) {
        return componentOf(position) < 0;
    }
}
