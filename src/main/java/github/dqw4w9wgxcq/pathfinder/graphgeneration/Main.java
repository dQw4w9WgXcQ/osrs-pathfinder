package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
class Main {
    public static class ExitCodes {
        private static final int BASE = 500;
        private static int i = 1;//todo use fixed vlaues
        public static final int ARGS_MALFORMED = BASE + i++;
        public static final int CACHE_OR_XTEAS_NOT_FOUND = BASE + i++;
        public static final int CACHE_READ_FAIL = BASE + i++;
        public static final int XTEAS_READ_FAIL = BASE + i++;
        public static final int XTEAS_MALFORMED = BASE + i++;
        public static final int WRITE_FAIL = BASE + i++;
    }

    public static final File DESKTOP_DIR = new File(System.getProperty("user.home"), "Desktop");
    public static final File DEFAULT_CACHE_DIR = new File(DESKTOP_DIR, "cache");
    public static final File DEFAULT_XTEAS_FILE = new File(DESKTOP_DIR, "xteas.json");
    public static final File DEFAULT_OUT_DIR = new File(DESKTOP_DIR, "graph");

    public static void main(String... args) {
        var outFileOpt = new Option("out", true, "Output directory");
        var cacheOpt = new Option("cache", true, "path to osrs cache dir.  the game populates at C:\\Users\\user\\jagexcache\\oldschool\\LIVE\\");
        var xteasOpt = new Option("xteas", true, "path to xteas json file");

        var options = new Options();
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
            cacheDir = DEFAULT_CACHE_DIR;
        }

        File xteasFile;
        if (cmd.hasOption("xteas")) {
            xteasFile = new File(cmd.getOptionValue("xteas"));
        } else {
            xteasFile = DEFAULT_XTEAS_FILE;
        }

        File outDir;
        if (cmd.hasOption("out")) {
            outDir = new File(cmd.getOptionValue("out"));
        } else {
            outDir = DEFAULT_OUT_DIR;
        }

        //load game data from cacheDir/xteasFile
        CacheData cacheData;
        try {
            cacheData = CacheData.load(cacheDir, xteasFile);
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

        var graph = Graph.generate(cacheData);

//        try {
//            graph.save(outDir);
//        } catch (IOException e) {
//            log.error("saving graph failed");
//            log.info(null, e);
//            System.exit(ExitCodes.WRITE_FAIL);
//            return;
//        }
    }
}
