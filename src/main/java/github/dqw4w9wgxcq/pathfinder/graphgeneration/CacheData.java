package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
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

/**
 * eager loads data from runelite cache tools. no fs io happens after the constructor
 *
 * @see RegionLoader
 * @see ObjectManager
 */
@Slf4j
public class CacheData {
    @Getter
    private final Map<Integer, Region> regions;
    @Getter
    private final int highestBaseX, highestBaseY;
    @Getter
    private final Map<Integer, ObjectDefinition> objectDefinitions;

    /**
     * @param cacheDir  directory containing raw osrs game cache.  <p>
     *                  the game populates this directory at [userhome]/jagexcache/oldschool/LIVE/
     * @param xteasJson json file containing json array of xteas
     * @throws FileNotFoundException cacheDir(or expected contents) or xteasJson doesn't exist
     * @throws IOException           some other fs error while reading cache with runelite utils (Store, RegionLoader, ObjectManager)
     * @throws JsonIOException       gson fs error reading xteas
     * @throws JsonSyntaxException   gson says xteas malformed
     * @see net.runelite.cache.util.XteaKey for xtea key format ([XteaKey, XteaKey, ...])
     */
    public CacheData(File cacheDir, File xteasJson) throws FileNotFoundException, IOException, JsonIOException, JsonSyntaxException {
        var store = new Store(cacheDir);
        store.load();

        var keyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            keyManager.loadKeys(is);
        }

        var regionLoader = new RegionLoader(store, keyManager);
        regionLoader.loadRegions();
        regionLoader.calculateBounds();
        var regions = regionLoader.getRegions();
        this.regions = new HashMap<>(regions.size());
        for (var region : regions) {
            System.out.println(region.getRegionX() + "," + region.getRegionY());
            this.regions.put(region.getRegionID(), region);
        }
        highestBaseX = regionLoader.getHighestX().getBaseX();
        highestBaseY = regionLoader.getHighestY().getBaseY();

        var objectManager = new ObjectManager(store);
        objectManager.load();
        //no way to access the internal map of id:definition directly
        var definitions = objectManager.getObjects();
        objectDefinitions = new HashMap<>(definitions.size());
        for (var definition : definitions) {
            objectDefinitions.put(definition.getId(), definition);
        }

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }
    }
}
