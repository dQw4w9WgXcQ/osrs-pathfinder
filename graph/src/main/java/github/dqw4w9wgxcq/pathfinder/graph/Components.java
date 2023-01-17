package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;

import java.util.List;

public record Components(int[][][] grid, List<List<Link>> links) {
    public int component(Position position){
        return grid[position.plane()][position.x()][position.y()];
    }
}
