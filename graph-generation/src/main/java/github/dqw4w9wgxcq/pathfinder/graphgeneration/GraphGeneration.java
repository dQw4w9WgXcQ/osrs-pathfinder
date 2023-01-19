package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import github.dqw4w9wgxcq.pathfinder.graph.store.GraphStore;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.Components;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.LinkedComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages.LeafletImages;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.FindLinks;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class GraphGeneration {
    public static void main(String[] args) {
        var cacheOpt = new Option("c", "cache", true, "Path to osrs cache dir that the game populates at C:\\Users\\user\\jagexcache\\oldschool\\LIVE\\");
        var xteasOpt = new Option("x", "xteas", true, "Path to xteas JSON file");
        var outOpt = new Option("o", "out", true, "Output directory");
        var leafletOpt = new Option("l", "leaflet", false, "Generate leaflet images");
        var otherDataOpt = new Option("d", "data", false, "Generate other data (objects, items, etc.)");
        var skipGraphOpt = new Option("s", "skip-graph", false, "Skip graph output (-l is now required)");

        cacheOpt.setRequired(true);
        xteasOpt.setRequired(true);

        var options = new Options();
        options.addOption(cacheOpt);
        options.addOption(xteasOpt);
        options.addOption(outOpt);
        options.addOption(leafletOpt);
        options.addOption(otherDataOpt);
        options.addOption(skipGraphOpt);

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("pathfinder", options);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("skip-graph") && !cmd.hasOption("leaflet")) {
            System.out.println("Cannot skip graph output if not generating leaflet images");
            new HelpFormatter().printHelp("pathfinder", options);
            System.exit(1);
            return;
        }

        var cacheDir = new File(cmd.getOptionValue("cache"));
        if (!cacheDir.exists()) {
            System.out.println("Cache dir does not exist");
            System.exit(1);
            return;
        }

        var xteasFile = new File(cmd.getOptionValue("xteas"));
        if (!xteasFile.exists()) {
            System.out.println("Xteas file does not exist");
            System.exit(1);
            return;
        }

        var outDir = new File(cmd.getOptionValue("out", System.getProperty("user.dir")));
        //noinspection ResultOfMethodCallIgnored
        outDir.mkdirs();

        CacheData cacheData;
        try {
            cacheData = CacheData.load(cacheDir, xteasFile);
        } catch (FileNotFoundException e) {
            log.error(null, e);
            System.out.println("cache dir missing expected content");
            System.exit(1);
            return;
        } catch (IOException e) {
            log.error(null, e);
            System.out.println("reading cache data failed");
            System.exit(1);
            return;
        } catch (JsonIOException e) {
            log.error(null, e);
            System.out.println("reading xtea failed");
            System.exit(1);
            return;
        } catch (JsonSyntaxException e) {
            log.error(null, e);
            System.out.println("xteas json malformed");
            System.exit(1);
            return;
        }

        var objectLocations = cacheData.regionData().getLocationsAdjustedFor0x2();
        var tileWorld = TileWorld.create(cacheData, objectLocations);
        var contiguousComponents = ContiguousComponents.create(tileWorld.getPlanes());
        var componentGrid = Components.createGrid(contiguousComponents);

        if (cmd.hasOption("leaflet")) {
            try {
                LeafletImages.write(new File(outDir, "leaflet"), cacheDir, xteasFile, componentGrid);
            } catch (IOException e) {
                log.error(null, e);
                System.out.println("writing leaflet images failed");
                System.exit(1);
                return;
            }
        }

        if (cmd.hasOption("skip-graph")) {
            log.info("Skipping graph");
            return;
        }

        var links = FindLinks.find(cacheData, objectLocations, componentGrid, tileWorld);
        var linkedComponents = LinkedComponents.create(contiguousComponents, links);
        var componentGraph = Components.createGraph(linkedComponents, contiguousComponents);

        try {
            new GraphStore(contiguousComponents.planes(), componentGraph, links).save(outDir);
        } catch (IOException e) {
            log.error(null, e);
            System.out.println("writing graph failed");
            System.exit(1);
            return;
        }

        log.info("done");
    }
}
