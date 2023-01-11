package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import java.util.List;

public interface Edge<T extends Edge<T>> extends Comparable<T> {
    List<T> adjacent();

    int cost();

    default int compareTo(T other) {
        return cost() - other.cost();
    }
}
