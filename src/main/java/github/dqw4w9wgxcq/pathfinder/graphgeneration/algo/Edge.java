package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import javax.annotation.Nonnull;
import java.util.List;

public interface Edge extends Comparable<Edge> {
    List<Edge> adjacent();

    int cost();

    default int compareTo(@Nonnull Edge other) {
        return cost() - other.cost();
    }
}
