package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Builder;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Builder(access = AccessLevel.PRIVATE)
public record CacheData(
        Map<Integer, Region> regions,
        int highestBaseX,
        int highestBaseY,
        Map<Integer, ObjectDefinition> objectDefinitions
) {

    /**
     * loads data from runelite cache tools. no fs io happens after
     *
     * @param cacheDir  directory containing raw osrs game cache.  <p>
     *                  the game populates this directory at [userhome]/jagexcache/oldschool/LIVE/
     * @param xteasJson json file containing json array of xteas
     * @throws FileNotFoundException cacheDir(or expected contents) or xteasJson doesn't exist
     * @throws IOException           some other fs error while reading cache with runelite utils (Store, RegionLoader, ObjectManager)
     * @throws JsonIOException       gson fs error reading xteas
     * @throws JsonSyntaxException   gson says xteas malformed
     * @see net.runelite.cache.util.XteaKey for xtea key format ([XteaKey, XteaKey, ...])
     */
    public static CacheData load(File cacheDir, File xteasJson) throws FileNotFoundException, IOException, JsonIOException, JsonSyntaxException {
        var b = new CacheDataBuilder();

        var store = new Store(cacheDir);
        store.load();

        var keyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            keyManager.loadKeys(is);
        }

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
        b.regions(regions);
        b.highestBaseX(regionLoader.getHighestX().getBaseX());
        b.highestBaseY(regionLoader.getHighestY().getBaseY());

        var objectManager = new ObjectManager(store);
        objectManager.load();
        //cant access the internal map
        var definitions = objectManager.getObjects();
        var objectDefinitions = new HashMap<Integer, ObjectDefinition>(definitions.size());
        for (var definition : definitions) {
            objectDefinitions.put(definition.getId(), definition);
        }
        b.objectDefinitions(objectDefinitions);

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }

        return b.build();
    }

    @VisibleForTesting
    private static void addFromRegionLoader(CacheDataBuilder b, RegionLoader regionLoader) {

    }
}
