package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.underground;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import net.runelite.cache.region.Position;

public record UndergroundLink(Position destination, int cost, int objectId, String action) implements Link {
}
