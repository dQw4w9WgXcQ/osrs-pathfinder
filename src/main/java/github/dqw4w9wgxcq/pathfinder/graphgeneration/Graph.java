package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.door.DoorLinks;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public record Graph() {
    public static Graph generate(CacheData data) {
        var gridWorld = GridWorld.create(data.regions(), data.highestWorldX(), data.highestWorldY(), data.objectDefinitions());

        var componentsPlanes = Arrays.stream(gridWorld.getPlanes())
                .parallel()
                .map(ContiguousComponents::findIn)
                .toList();

        var doorLinks = DoorLinks.find(data.objectDefinitions(), data.regions());

        return new Graph();
    }
}
