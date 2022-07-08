package scratchpaper;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//copy pasted from
//https://github.com/runelite/runelite/blob/ec9b3dfb84d1a13552721e7e2108f87977b70400/cache/src/test/java/net/runelite/cache/MapDumperTest.java
@SuppressWarnings("ALL")
@Slf4j
public class Lolololol {
    static File desktopDir = new File(System.getProperty("user.home"), "Desktop");
    static File cacheDir = new File(desktopDir, "cache");

    static final int MAX_REGIONS = 32768;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        var maxMem = Runtime.getRuntime().maxMemory();
        var totalMem = Runtime.getRuntime().totalMemory();
        var freeMem = Runtime.getRuntime().freeMemory();
        var usedMem = totalMem - freeMem;
        log.info("Max memory: {}", maxMem / 1024 / 1024);
        log.info("Total memory: {}", totalMem / 1024 / 1024);
        log.info("Free memory: {}", freeMem / 1024 / 1024);
        log.info("Used memory: {}", usedMem / 1024 / 1024);
        log.info("mem usage: {}", (totalMem - freeMem) / (double) totalMem);
        Map<MapDefinition, LocationsDefinition> regions;
        try {
            try (Store store = new Store(cacheDir)) {
                {
                    var before = System.currentTimeMillis();
                    store.load();
                    var after = System.currentTimeMillis();
                    log.info("Loaded store in {}ms", after - before);
                }

                var before = System.currentTimeMillis();
                regions = loadRegions(store);
                var after = System.currentTimeMillis();
                log.info("Loaded regions in {}ms", after - before);
            }
        } catch (Exception e) {
            log.error(">:(", e);
            return;
        }
        var maxMem2 = Runtime.getRuntime().maxMemory();
        var totalMem2 = Runtime.getRuntime().totalMemory();
        var freeMem2 = Runtime.getRuntime().freeMemory();
        var usedMem2 = totalMem2 - freeMem2;
        log.info("Max memory: {}", maxMem2 / 1024 / 1024);
        log.info("Total memory: {}", totalMem2 / 1024 / 1024);
        log.info("Free memory: {}", freeMem2 / 1024 / 1024);
        log.info("Used memory: {}", usedMem2 / 1024 / 1024);
        log.info("mem usage: {}", (totalMem2 - freeMem2) / (double) totalMem2);
        for (Map.Entry<MapDefinition, LocationsDefinition> entry : regions.entrySet()) {
            //System.out.println(entry.getKey().getRegionX() + "_" + entry.getKey().getRegionY());
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<MapDefinition, LocationsDefinition> loadRegions(Store store) throws IOException {
        Map<MapDefinition, LocationsDefinition> mapMap = new HashMap<>();
        Storage storage = store.getStorage();
        Index index = store.getIndex(IndexType.MAPS);
        XteaKeyManager keyManager = new XteaKeyManager();
        keyManager.loadKeys(new FileInputStream(new File(desktopDir, "xteas.json")));

        for (int i = 0; i < MAX_REGIONS; ++i) {
            int x = i >> 8;
            int y = i & 0xFF;

            Archive map = index.findArchiveByName("m" + x + "_" + y);
            Archive land = index.findArchiveByName("l" + x + "_" + y);

            assert (map == null) == (land == null);

            if (map == null || land == null) {
                //log.info("Skipping region {},{}", x,y);
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

    //    @Test
//    @Ignore
    public static void dumpJson() throws IOException {
        File outDir = new File(desktopDir, "maps");
        outDir.mkdir();

        try (Store store = new Store(cacheDir)) {
            store.load();

            Map<MapDefinition, LocationsDefinition> regions = loadRegions(store);

            for (Map.Entry<MapDefinition, LocationsDefinition> entry : regions.entrySet()) {
                MapDefinition key = entry.getKey();
                LocationsDefinition value = entry.getValue();

                int x = key.getRegionX();
                int y = key.getRegionY();
                log.info("{} {}", x, y);
                Files.write(gson.toJson(key).getBytes(), new File(outDir, "m" + x + "_" + y + ".json"));
                if (value != null) {
                    Files.write(gson.toJson(value).getBytes(), new File(outDir, "l" + x + "_" + y + ".json"));
                } else {
                    log.info("No locations for region " + x + "_" + y);
                }
            }
        }

        log.info("Dumped regions to {}", outDir);
    }
}
