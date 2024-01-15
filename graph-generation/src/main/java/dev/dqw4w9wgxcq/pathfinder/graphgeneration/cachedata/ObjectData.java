package dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata;

import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.fs.Store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public record ObjectData(Map<Integer, ObjectDefinition> definitions) {
    static ObjectData load(Store store) throws IOException {
        var objectManager = new ObjectManager(store);
        objectManager.load();

        log.info("loaded {} object definitions", objectManager.getObjects().size());

        // cant access the internal map, so have to reconstruct it
        var definitionsCol = objectManager.getObjects();
        var definitions = new HashMap<Integer, ObjectDefinition>(definitionsCol.size());
        for (var definition : definitionsCol) {
            definitions.put(definition.getId(), definition);
        }

        return new ObjectData(definitions);
    }
}
