package dev.dqw4w9wgxcq.pathfinder.commons.domain.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public sealed interface Link permits DoorLink, DungeonLink, ShipLink, SpecialLink, StairLink, TeleportLink, WildernessDitchLink {
    Type type();

    /**
     * index of the link
     */
    int id();

    Position origin();

    Position destination();

    /**
     * approximate cost in terms of walking tile units
     */
    int cost();

    List<Requirement> requirements();

    enum Type {
        DOOR,
        STAIR,
        DUNGEON,
        SHIP,
        WILDERNESS_DITCH,
        SPECIAL,
        TELEPORT,
        ;
    }
}
