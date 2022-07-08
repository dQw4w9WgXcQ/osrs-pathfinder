package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.domain.Graph;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.LocationsDefinition;
import net.runelite.cache.definitions.MapDefinition;
import net.runelite.cache.definitions.loaders.LocationsLoader;
import net.runelite.cache.definitions.loaders.MapLoader;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.XteaKeyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GraphGenerator {
    private static final int MAX_REGIONS = 32768;

    private final Store store;
    private final XteaKeyManager keyManager;

    /**
     * @param cacheDir  raw osrs game cache.  the game populates this directory at C:\Users\user\jagexcache\oldschool\LIVE\
     * @param xteasJson json file containing json array of xteas {@see net.runelite.cache.util.XteaKey}
     * @throws FileNotFoundException if the cacheDir(or expected contents) or xteasJson don't exist
     * @throws IOException           failed initing or loading store {@see net.runelite.cache.fs.Store#Store(File)} {@see net.runelite.cache.fs.Store#load()}
     * @throws JsonIOException       gson failed loading xteas {@see net.runelite.cache.util.XteaKeyManager#loadKeys()}
     * @throws JsonSyntaxException   gson says xteas malformed {@see net.runelite.cache.util.XteaKeyManager#loadKeys(File)}
     */
    public GraphGenerator(File cacheDir, File xteasJson) throws IOException {
        try {
            store = new Store(cacheDir);
        } catch (IOException e) {
            log.warn("failed initing store");
            throw e;
        }

        try {
            store.load();
        } catch (IOException e) {
            log.warn("failed loading store");
            throw e;
        }

        keyManager = new XteaKeyManager();

        try (var is = new FileInputStream(xteasJson)) {
            try {
                keyManager.loadKeys(is);
            } catch (JsonIOException e) {
                log.warn("exception initing xteaKeyManager");
                throw e;
            } catch (JsonSyntaxException e) {
                log.warn("malformed xteas");
                throw e;
            }
        } catch (FileNotFoundException e) {
            log.warn("xteas json file doesn't exist");
            throw e;
        }
    }

    /**
     * @throws IOException if an error occurs reading the cache
     */
    public Graph generate() throws IOException {
        var storage = store.getStorage();
        var index = store.getIndex(IndexType.MAPS);

        var regions = loadRegions();

        return new Graph();
    }

    private Map<MapDefinition, LocationsDefinition> loadRegions() throws IOException {
        Map<MapDefinition, LocationsDefinition> mapMap = new HashMap<>();
        Storage storage = store.getStorage();
        Index index = store.getIndex(IndexType.MAPS);

        for (int i = 0; i < MAX_REGIONS; ++i) {
            int x = i >> 8;
            int y = i & 0xFF;

            Archive map = index.findArchiveByName("m" + x + "_" + y);
            Archive land = index.findArchiveByName("l" + x + "_" + y);

            assert (map == null) == (land == null);

            //noinspection ConstantConditions
            if (map == null || land == null) {
                continue;
            }

            byte[] data = map.decompress(storage.loadArchive(map));
            MapDefinition mapDef = new MapLoader().load(x, y, data);
            LocationsDefinition locDef = null;

            int[] keys = keyManager.getKey(i);
            if (keys != null) {
                try {
                    data = land.decompress(storage.loadArchive(land), keys);
                } catch (IOException ex) {
                    continue;
                }

                locDef = new LocationsLoader().load(x, y, data);
            }

            mapMap.put(mapDef, locDef);
        }

        return mapMap;
    }
}
