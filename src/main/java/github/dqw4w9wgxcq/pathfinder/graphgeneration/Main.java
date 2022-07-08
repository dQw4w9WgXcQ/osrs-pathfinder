package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.domain.Graph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class Main {
    public static class ExitCodes {
        private static final int ARGS_BASE = 500;
        private static int i = 0;
        public static final int MALFORMED_ARGS = ARGS_BASE + i++;

        private static final int LOADING_BASE = 510;
        private static int j = 0;
        public static final int CACHE_OR_XTEA_FILES_MISSING = LOADING_BASE + j++;
        public static final int CACHE_LOAD_FAILED = LOADING_BASE + j++;
        public static final int XTEAS_LOAD_FAILED = LOADING_BASE + j++;
        public static final int XTEAS_JSON_MALFORMED = LOADING_BASE + j++;

        private static final int GENERATION_BASE = 520;
        private static int k = 0;
        public static final int GRAPH_GEN_FAILED = GENERATION_BASE + k++;
    }

    private static final File DESKTOP_DIR = new File(System.getProperty("user.home"), "Desktop");

    public static void main(String[] args) {
        var options = new Options();

        var cacheOpt = new Option("cache", true, "path to osrs cache dir.  the game populates at C:\\Users\\user\\jagexcache\\oldschool\\LIVE\\");
        options.addOption(cacheOpt);

        var xteasOpt = new Option("xteas", true, "path to json file containing xteas");
        options.addOption(xteasOpt);

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            log.debug(null, e);
            System.exit(ExitCodes.MALFORMED_ARGS);
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

        GraphGenerator graphGenerator;
        try {
            graphGenerator = new GraphGenerator(cacheDir, xteasFile);
        } catch (FileNotFoundException e) {
            log.error(null, e);
            System.exit(ExitCodes.CACHE_OR_XTEA_FILES_MISSING);
            return;
        } catch (IOException e) {
            log.error(null, e);
            System.exit(ExitCodes.CACHE_LOAD_FAILED);
            return;
        } catch (JsonIOException e) {
            log.error(null, e);
            System.exit(ExitCodes.XTEAS_LOAD_FAILED);
            return;
        } catch (JsonSyntaxException e) {
            log.error(null, e);
            System.exit(ExitCodes.XTEAS_JSON_MALFORMED);
            return;
        }

        Graph graph;
        try {
            graph = graphGenerator.generate();
        } catch (Exception e) {
            log.error(null, e);
            System.exit(ExitCodes.GRAPH_GEN_FAILED);
            return;
        }

        log.info("generated graph:" + graph);
    }
}
