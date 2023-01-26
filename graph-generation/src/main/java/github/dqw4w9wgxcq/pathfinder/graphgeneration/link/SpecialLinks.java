package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import github.dqw4w9wgxcq.pathfinder.commons.domain.link.SpecialLink;
import net.runelite.api.ObjectID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecialLinks {
    private static final String OBJECT_ID = "objectId";
    private static final String ACTION = "action";

    public static List<SpecialLink> find() {
        var links = new ArrayList<SpecialLink>();

        var id = 0;

        var treeGnomeVillageLooseRailing = new SpecialLink(
                id++,
                new Position(2515, 3160, 0),
                new Position(2515, 3161, 0),
                10,
                List.of(),
                Map.of(
                        OBJECT_ID, ObjectID.LOOSE_RAILING_2186,
                        ACTION, "Squeeze-through"
                )
        );
        links.add(treeGnomeVillageLooseRailing);
        links.add(bidirection(id++, treeGnomeVillageLooseRailing));

        var tollGate = new SpecialLink(
                id++,
                new Position(3267, 3227, 0),
                new Position(3268, 3227, 0),
                10,
//                List.of(new ItemRequirement(ItemID.COINS_995, 10)),
                List.of(),//todo fix requirement serialization
                Map.of(
                        OBJECT_ID, ObjectID.GATE_44052,
                        ACTION, "Pay-toll(10gp)"
                )
        );
        links.add(tollGate);
        links.add(bidirection(id++, tollGate));

        var tempMortania = new SpecialLink(id++, new Position(3404, 3503, 0), new Position(3404, 9904, 0), 10, List.of(), Map.of());
        var tempMortaniaOut = new SpecialLink(id++, new Position(3440, 9887, 0), new Position(3439, 3485, 0), 10, List.of(), Map.of());
        links.add(tempMortania);
        links.add(tempMortaniaOut);

        return links;
    }

    public static SpecialLink bidirection(int id, SpecialLink link) {
        return new SpecialLink(
                id,
                link.destination(),
                link.origin(),
                link.cost(),
                link.requirements(),
                link.extra()
        );
    }
}
