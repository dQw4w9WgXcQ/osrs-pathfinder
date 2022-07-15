package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import net.runelite.cache.region.Position;

public record DoorLink(Position destination) implements Link {
    @Override
    public int cost() {
        return 10;
    }
}
