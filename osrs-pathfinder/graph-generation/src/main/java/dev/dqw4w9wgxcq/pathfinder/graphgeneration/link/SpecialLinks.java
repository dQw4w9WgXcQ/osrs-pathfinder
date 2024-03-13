package dev.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.SpecialLink;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.ItemRequirement;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.QuestRequirement;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedAssignment")
@Slf4j
public class SpecialLinks {
    private static final String OBJECT_ID = "objectId";
    private static final String ACTION = "action";

    public static List<SpecialLink> find() {
        log.info("special links");
        var links = new ArrayList<SpecialLink>();

        var id = 0;

        var treeGnomeVillageLooseRailing = new SpecialLink(
                id++,
                new Position(2515, 3160, 0),
                new Position(2515, 3161, 0),
                10,
                List.of(),
                Map.of(OBJECT_ID, ObjectID.LOOSE_RAILING_2186, ACTION, "Squeeze-through"));
        links.add(treeGnomeVillageLooseRailing);
        links.add(bidirection(id++, treeGnomeVillageLooseRailing));

        var tollGate = new SpecialLink(
                id++,
                new Position(3267, 3227, 0),
                new Position(3268, 3227, 0),
                10,
                List.of(new ItemRequirement(true, ItemID.COINS_995, 10)),
                Map.of(OBJECT_ID, ObjectID.GATE_44052, ACTION, "Pay-toll(10gp)"));
        links.add(tollGate);
        links.add(bidirection(id++, tollGate));

        var drezelHolyBarrier = new SpecialLink(
                id++,
                new Position(3440, 9887, 0),
                new Position(3439, 3485, 0),
                10,
                List.of(new QuestRequirement(Quest.PRIEST_IN_PERIL)),
                Map.of(OBJECT_ID, ObjectID.HOLY_BARRIER, ACTION, "Pass-through"));
        links.add(drezelHolyBarrier);

        log.info("found {} special links", links.size());
        return links;
    }

    public static SpecialLink bidirection(int id, SpecialLink link) {
        return new SpecialLink(id, link.end(), link.start(), link.cost(), link.requirements(), link.extra());
    }
}