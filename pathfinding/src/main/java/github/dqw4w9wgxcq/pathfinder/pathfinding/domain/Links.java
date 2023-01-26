package github.dqw4w9wgxcq.pathfinder.pathfinding.domain;

import github.dqw4w9wgxcq.pathfinder.commons.domain.link.*;

import java.util.ArrayList;
import java.util.List;

public record Links(
        List<DoorLink> doorLinks,
        List<StairLink> stairLinks,
        List<DungeonLink> dungeonLinks,
        List<ShipLink> shipLinks,
        List<WildernessDitchLink> wildernessDitchLinks,
        List<SpecialLink> specialLinks
) {
    public List<Link> all() {
        var out = new ArrayList<Link>();
        out.addAll(doorLinks);
        out.addAll(stairLinks);
        out.addAll(dungeonLinks);
        out.addAll(shipLinks);
        out.addAll(wildernessDitchLinks);
        out.addAll(specialLinks);
        return out;
    }

    public Link getLink(LinkType type, int id) {
        var link = switch (type) {
            case DOOR -> doorLinks.get(id);
            case STAIR -> stairLinks.get(id);
            case DUNGEON -> dungeonLinks.get(id);
            case SHIP -> shipLinks.get(id);
            case WILDERNESS_DITCH -> wildernessDitchLinks.get(id);
            case SPECIAL -> specialLinks.get(id);
        };

        assert link != null;

        assert link.id() == id;

        return link;
    }
}
