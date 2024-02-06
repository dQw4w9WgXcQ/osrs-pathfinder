package dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Agent;

import java.util.HashMap;

public record ItemRequirement(Type type, boolean consumes, int id, int amount) implements Requirement {
    public ItemRequirement(boolean consumes, int id, int amount) {
        this(Type.ITEM, consumes, id, amount);
    }

    @Override
    public boolean check(Agent agent) {
        var items = agent.items();
        if (items == null) return true;

        return items.getOrDefault(id, 0) >= amount;
    }

    // todo find a way to not copy the entire item map?
    public Agent affectAgent(Agent agent) {
        if (!consumes) return Requirement.super.affectAgent(agent);

        var items = agent.items();
        if (items == null) return agent;

        var newItems = new HashMap<>(items);
        var newAmount = newItems.getOrDefault(id, 0) - this.amount;
        if (newAmount < 0) {
            throw new IllegalStateException("newAmount " + newAmount + " ItemRequirement " + this);
        } else if (newAmount == 0) {
            newItems.remove(id);
        } else {
            newItems.put(id, newAmount);
        }

        return agent.withItems(newItems);
    }
}
