package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.fs.Store;
import net.runelite.cache.region.RegionLoader;
import net.runelite.cache.util.XteaKeyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * inits and eager loads runelite cache utils.
 * no fs after the constructor.  close() is called on Store and further fs operations on held runelite loaders/managers will fail.
 */
@Slf4j
public class CacheData {
    @Getter
    private final RegionLoader regionLoader;
    @Getter
    private final ObjectManager objectManager;

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
        Store store = new Store(cacheDir);
        store.load();

        var keyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            keyManager.loadKeys(is);
        }

        regionLoader = new RegionLoader(store, keyManager);
        regionLoader.loadRegions();
        regionLoader.calculateBounds();

        objectManager = new ObjectManager(store);
        objectManager.load();

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }
    }
}
