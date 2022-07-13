package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.util.RegionUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.fs.Store;
import net.runelite.cache.region.Region;
import net.runelite.cache.region.RegionLoader;
import net.runelite.cache.util.XteaKeyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public record CacheData(
        Map<Integer, Region> regions,
        int highestWorldX,
        int highestWorldY,
        Map<Integer, ObjectDefinition> objectDefinitions
) {
    public CacheData(
            Map<Integer, Region> regions,
            int highestWorldX,
            int highestWorldY,
            Map<Integer, ObjectDefinition> objectDefinitions
    ) {
        this.regions = Collections.unmodifiableMap(regions);
        this.highestWorldX = highestWorldX;
        this.highestWorldY = highestWorldY;
        this.objectDefinitions = Collections.unmodifiableMap(objectDefinitions);
    }

    /**
     * loads data from runelite cache tools. no fs io happens after
     *
     * @param cacheDir  directory containing raw osrs game cache.  <p>
     *                  the game populates this directory at [userhome]/jagexcache/oldschool/LIVE/
     * @param xteasJson json file containing json array of xteas
     * @throws FileNotFoundException cacheDir(or expected contents) or xteasJson doesn't exist
     * @throws IOException           fs error while reading cache with runelite utils (Store, RegionLoader, ObjectManager)
     * @throws JsonIOException       gson fs error reading xteas
     * @throws JsonSyntaxException   gson says xteas malformed
     * @see net.runelite.cache.util.XteaKey for xtea key format ([XteaKey, XteaKey, ...])
     */
    public static CacheData load(File cacheDir, File xteasJson) throws FileNotFoundException, IOException, JsonIOException, JsonSyntaxException {
        var store = new Store(cacheDir);
        store.load();

        var keyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            keyManager.loadKeys(is);
        }

        var regionData = loadRegionData(store, keyManager);
        var objectDefinitions = loadObjectData(store);

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }

        return new CacheData(regionData.regions(), regionData.highestWorldX(), regionData.highestWorldY(), objectDefinitions);
    }

    record RegionData(Map<Integer, Region> regions, int highestWorldX, int highestWorldY) {
    }

    static RegionData loadRegionData(Store store, XteaKeyManager keyManager) throws IOException {
        var regionLoader = new RegionLoader(store, keyManager);
        regionLoader.loadRegions();
        regionLoader.calculateBounds();
        //cant access the internal map
        var regionsCol = regionLoader.getRegions();
        var regions = new HashMap<Integer, Region>(regionsCol.size());
        for (var region : regionsCol) {
            log.debug("adding region " + region.getRegionID());
            regions.put(region.getRegionID(), region);
        }

        return new RegionData(
                regions,
                regionLoader.getHighestX().getBaseX() + RegionUtil.SIZE,
                regionLoader.getHighestY().getBaseY() + RegionUtil.SIZE
        );
    }

    static Map<Integer, ObjectDefinition> loadObjectData(Store store) throws IOException {
        var objectManager = new ObjectManager(store);
        objectManager.load();
        //cant access the internal map
        var definitionsCol = objectManager.getObjects();
        var definitions = new HashMap<Integer, ObjectDefinition>(definitionsCol.size());
        for (var definition : definitionsCol) {
            definitions.put(definition.getId(), definition);
        }

        return definitions;
    }
}
