package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

public enum LinkType {
    DOOR,
    STAIR,
    DUNGEON,
    SHIP,
    WILDERNESS_DITCH,
    SPECIAL,
    ;

    public static LinkType of(Link link) {
        Class<? extends Link> clazz = link.getClass();
        if (clazz.equals(DoorLink.class)) {
            return DOOR;
        } else if (clazz.equals(StairLink.class)) {
            return STAIR;
        } else if (clazz.equals(DungeonLink.class)) {
            return DUNGEON;
        } else if (clazz.equals(ShipLink.class)) {
            return SHIP;
        } else if (clazz.equals(WildernessDitchLink.class)) {
            return WILDERNESS_DITCH;
        } else if (clazz.equals(SpecialLink.class)) {
            return SPECIAL;
        }

        throw new IllegalArgumentException("Unknown link type: " + link);
    }
}
