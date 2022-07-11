package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.grid.TileGrid;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.utils.RegionUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Region;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
class Main {
    public static class ExitCodes {
        private static final int BASE = 500;
        private static int i = 0;//todo use fixed vlaues
        public static final int ARGS_MALFORMED = BASE + i++;
        public static final int CACHE_OR_XTEAS_NOT_FOUND = BASE + i++;
        public static final int CACHE_READ_FAIL = BASE + i++;
        public static final int XTEAS_READ_FAIL = BASE + i++;
        public static final int XTEAS_MALFORMED = BASE + i++;
    }

    public static final File DESKTOP_DIR = new File(System.getProperty("user.home"), "Desktop");

    public static void main(String... args) {
        var options = new Options();

        var outFileOpt = new Option("out", true, "Output directory");
        var cacheOpt = new Option("cache", true, "path to osrs cache dir.  the game populates at C:\\Users\\user\\jagexcache\\oldschool\\LIVE\\");
        var xteasOpt = new Option("xteas", true, "path to xteas json file");
        options.addOption(outFileOpt);
        options.addOption(cacheOpt);
        options.addOption(xteasOpt);

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            log.info(null, e);
            System.exit(ExitCodes.ARGS_MALFORMED);
            return;
        }

        File cacheDir;
        if (cmd.hasOption("cache")) {
            cacheDir = new File(cmd.getOptionValue("cache"));
        } else {
            cacheDir = new File(DESKTOP_DIR, "cache");
        }
        File xteasFile;
        if (cmd.hasOption("xteas")) {
            xteasFile = new File(cmd.getOptionValue("xteas"));
        } else {
            xteasFile = new File(DESKTOP_DIR, "xteas.json");
        }
        File outDir;
        if (cmd.hasOption("out")) {
            outDir = new File(cmd.getOptionValue("out"));
        } else {
            outDir = new File(DESKTOP_DIR, "graph");
        }

        CacheData cacheData;
        try {
            cacheData = new CacheData(cacheDir, xteasFile);
        } catch (FileNotFoundException e) {
            log.error("cache dir(or expected contents) or xteas file not found");
            log.info(null, e);
            System.exit(ExitCodes.CACHE_OR_XTEAS_NOT_FOUND);
            return;
        } catch (IOException e) {
            log.error("reading cache data failed");
            log.info(null, e);
            System.exit(ExitCodes.CACHE_READ_FAIL);
            return;
        } catch (JsonIOException e) {
            log.error("reading xtea failed");
            log.info(null, e);
            System.exit(ExitCodes.XTEAS_READ_FAIL);
            return;
        } catch (JsonSyntaxException e) {
            log.error("xteas json malformed");
            log.info(null, e);
            System.exit(ExitCodes.XTEAS_MALFORMED);
            return;
        }

        var highestBaseX = cacheData.getHighestBaseX();
        var highestBaseY = cacheData.getHighestBaseY();
        var worldSizeX = highestBaseX + RegionUtils.SIZE;
        var worldSizeY = highestBaseY + RegionUtils.SIZE;
        log.info("world size x: {}, y: {}", worldSizeX, worldSizeY);

        var regions = cacheData.getRegions();
        var definitions = cacheData.getObjectDefinitions();


    }
}
