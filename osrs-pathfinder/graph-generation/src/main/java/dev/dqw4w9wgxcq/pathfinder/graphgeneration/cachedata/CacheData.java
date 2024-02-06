package dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata;

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
public record CacheData(RegionData regionData, ObjectData objectData) {
    /**
     * Eager loads data from runelite cache tools. No filesystem IO happens after.
     *
     * @throws FileNotFoundException cacheDir(or expected contents) or xteasJson doesn't exist
     * @throws IOException           FS error while reading cache with runelite utils (Store, RegionLoader, ObjectManager)
     * @throws JsonIOException       Gson FS error reading xteas
     * @throws JsonSyntaxException   Gson says XTEAs malformed
     */
    public static CacheData load(File cacheDir, File xteasJson)
            throws FileNotFoundException, IOException, JsonIOException, JsonSyntaxException {
        var store = new Store(cacheDir);
        store.load();

        var xteaKeyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            xteaKeyManager.loadKeys(is);
        }

        var regionData = RegionData.load(store, xteaKeyManager);
        var objectData = ObjectData.load(store);

        try {
            store.close();
        } catch (IOException e) {
            log.warn("failed closing store", e);
        }

        return new CacheData(regionData, objectData);
    }
}
