package github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.XteaKeyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public record CacheData(
        RegionData regionData,
        ObjectData objectData
) {
    /**
     * loads data from runelite cache tools. no fs io happens after
     *
     * @param cacheDir  directory containing the osrs game cache.  the game client populates this directory at [userhome]/jagexcache/oldschool/LIVE/
     * @param xteasJson json file containing json array of xteas (format specified by net.runelite.cache.util.XteaKey)
     * @throws FileNotFoundException cacheDir(or expected contents) or xteasJson doesn't exist
     * @throws IOException           fs error while reading cache with runelite utils (Store, RegionLoader, ObjectManager)
     * @throws JsonIOException       gson fs error reading xteas
     * @throws JsonSyntaxException   gson says xteas malformed
     */
    public static CacheData load(File cacheDir, File xteasJson) throws FileNotFoundException, IOException, JsonIOException, JsonSyntaxException {
        var store = new Store(cacheDir);
        store.load();

        var xteaManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            xteaManager.loadKeys(is);
        }

        var regionData = RegionData.load(store, xteaManager);
        var objectData = ObjectData.load(store);

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }

        return new CacheData(regionData, objectData);
    }
}
