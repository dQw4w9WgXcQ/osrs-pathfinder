package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class Main {
    public static class ExitCodes {
        private static final int BASE = 500;
        private static int i = 0;
        public static final int ARGS_MALFORMED = BASE + i++;
        public static final int CACHE_OR_XTEAS_NOT_FOUND = BASE + i++;
        public static final int READ_FAIL = BASE + i++;
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
            log.debug(null, e);
            System.out.println(e.getMessage());
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
        File out;
        if (cmd.hasOption("out")) {
            out = new File(cmd.getOptionValue("out"));
        }else {
            out = DESKTOP_DIR;
        }

        CacheData cacheData;
        try {
            cacheData = new CacheData(cacheDir, xteasFile);
        } catch (FileNotFoundException e) {
            log.error("file not found exception creating cache data", e);
            System.out.println("cache dir(or expected contents) or xteas file not found");
            System.exit(ExitCodes.CACHE_OR_XTEAS_NOT_FOUND);
            return;
        } catch (IOException | JsonIOException e) {
            log.error("io error creating cache data", e);
            System.out.println("io error reading/loading cache or xteas");
            System.exit(ExitCodes.READ_FAIL);
            return;
        } catch (JsonSyntaxException e) {
            log.error("xteas json malformed", e);
            System.out.println("xteas file malformed");
            System.exit(ExitCodes.XTEAS_MALFORMED);
            return;
        }

        var graph = Graph.generate(cacheData);
    }
}
