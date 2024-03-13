package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.ShipLink;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedAssignment")
@Slf4j
public class ShipLinks {
    public static List<ShipLink> find() {
        log.info("finding ship links");

        var links = new ArrayList<ShipLink>();

        int id = 0;

        var portSarimToKaramja = new ShipLink(id++, new Position(3027, 3218, 0), new Position(2954, 3147, 0));
        addWithBidirection(id++, links, portSarimToKaramja);

        var ardougneToKaramja = new ShipLink(id++, new Position(2673, 3275, 0), new Position(2772, 3225, 0));
        addWithBidirection(id++, links, ardougneToKaramja);

        var portSarimToEntrana = new ShipLink(id++, new Position(3046, 3236, 0), new Position(2833, 3336, 0));
        addWithBidirection(id++, links, portSarimToEntrana);

        return links;
    }

    public static void addWithBidirection(int bidirectionalId, List<ShipLink> links, ShipLink link) {
        links.add(link);
        links.add(bidirection(bidirectionalId, link));
    }

    private static ShipLink bidirection(int id, ShipLink link) {
        return new ShipLink(id, link.end(), link.start());
    }
}
