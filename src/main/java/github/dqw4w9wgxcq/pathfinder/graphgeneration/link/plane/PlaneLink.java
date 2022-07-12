package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.plane;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import net.runelite.cache.region.Position;

public record PlaneLink(Position destination, int cost, int objectId, String action) implements Link {
}
