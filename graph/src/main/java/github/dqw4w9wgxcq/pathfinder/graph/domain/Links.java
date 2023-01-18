package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.link.*;

import java.util.ArrayList;
import java.util.List;

public record Links(
        List<DoorLink> doorLinks,
        List<StairLink> stairLinks,
        List<UndergroundLink> undergroundLinks,
        List<SpecialLink> specialLinks
) {
    public List<Link> all() {
        var out = new ArrayList<Link>();
        out.addAll(doorLinks);
        out.addAll(stairLinks);
        out.addAll(undergroundLinks);
        out.addAll(specialLinks);
        return out;
    }

    public Link getLink(LinkType type, int id) {
        var link = switch (type) {
            case DOOR -> doorLinks.get(id);
            case SPECIAL -> specialLinks.get(id);
            case STAIR -> stairLinks.get(id);
            default -> throw new IllegalArgumentException("unknown link type: " + type);
        };

        assert link != null;

        assert link.id() == id;

        return link;
    }
}
