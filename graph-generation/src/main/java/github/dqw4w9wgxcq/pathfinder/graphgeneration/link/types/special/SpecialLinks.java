package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.special;

import net.runelite.cache.region.Position;

import java.util.ArrayList;
import java.util.List;

public class SpecialLinks {
    public static List<SpecialLink> find() {
        var out = new ArrayList<SpecialLink>();

        //todo test
        out.add(new SpecialLink(new Position(3235, 3225, 0), new Position(2787, 3440, 0)));

        return out;
    }
}
