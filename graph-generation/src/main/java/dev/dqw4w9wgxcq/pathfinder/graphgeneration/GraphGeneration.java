package dev.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.commons.store.GraphStore;
import dev.dqw4w9wgxcq.pathfinder.commons.store.GridStore;
import dev.dqw4w9wgxcq.pathfinder.commons.store.LinkStore;
import dev.dqw4w9wgxcq.pathfinder.commons.store.StoreMeta;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.component.CreateComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.component.LinkedComponents;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages.LeafletImages;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.link.FindLinks;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class GraphGeneration {
    public static void main(String[] args) {
        var startTime = System.currentTimeMillis();

        var cacheMetaOpt = new Option("m", "cache-meta", true, "cache metadata string, defaults to null");
        var cacheOpt = new Option(
                "c",
                "cache",
                true,
                "Path to osrs cache dir that the game populates at C:\\Users\\user\\jagexcache\\oldschool\\LIVE\\  Defaults to ./cache");
        var xteasOpt = new Option("x", "xteas", true, "Path to xteas JSON file.  Defaults to ./xteas.json");
        var outOpt = new Option("o", "out", true, "Output directory.  Defaults to ./");
        var leafletOpt = new Option("l", "leaflet", false, "Generate leaflet images");
        var skipGraphOpt = new Option("s", "skip-graph", false, "Skip graph output (only links will be written)");

        var options = new Options();
        options.addOption(cacheMetaOpt);
        options.addOption(cacheOpt);
        options.addOption(xteasOpt);
        options.addOption(outOpt);
        options.addOption(leafletOpt);
        options.addOption(skipGraphOpt);

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("pathfinder", options);
            System.exit(1);
            return;
        }

        var cacheMeta = cmd.getOptionValue(cacheMetaOpt, null);
        if (cacheMeta == null) {
            log.warn("cacheMeta is null");
        } else {
            log.info("cacheMeta: {}", cacheMeta);
        }
        var storeMeta = new StoreMeta(null, cacheMeta);

        var cacheDir = new File(cmd.getOptionValue(cacheOpt, "cache"));
        if (!cacheDir.exists()) {
            System.err.println("Cache dir does not exist");
            System.exit(1);
            return;
        }

        var xteasFile = new File(cmd.getOptionValue(xteasOpt, "xteas.json"));
        if (!xteasFile.exists()) {
            System.err.println("Xteas file does not exist");
            System.exit(1);
            return;
        }

        var outDir = new File(cmd.getOptionValue(outOpt, System.getProperty("user.dir")));
        //noinspection ResultOfMethodCallIgnored
        outDir.mkdirs();

        CacheData cacheData;
        try {
            cacheData = CacheData.load(cacheDir, xteasFile);
        } catch (FileNotFoundException e) {
            log.debug("cache dir missing expected content", e);
            System.err.println("cache dir missing expected content");
            System.exit(1);
            return;
        } catch (IOException e) {
            log.debug("reading cache data failed", e);
            System.err.println("reading cache data failed");
            System.exit(1);
            return;
        } catch (JsonIOException e) {
            log.debug("reading xtea failed", e);
            System.err.println("reading xtea failed");
            System.exit(1);
            return;
        } catch (JsonSyntaxException e) {
            log.debug("xteas json malformed", e);
            System.err.println("xteas json malformed");
            System.exit(1);
            return;
        }

        var objectLocations = cacheData.regionData().getLocationsAdjustedFor0x2();
        var tileWorld = TileWorld.create(cacheData, objectLocations);
        var contiguousComponents = ContiguousComponents.create(tileWorld.getPlanes());
        var componentGrid = new ComponentGrid(contiguousComponents.planes());

        if (cmd.hasOption(leafletOpt)) {
            try {
                LeafletImages.write(new File(outDir, "leaflet"), cacheDir, xteasFile, componentGrid);
            } catch (IOException e) {
                log.debug("writing leaflet images failed", e);
                System.err.println("writing leaflet images failed");
                System.exit(1);
                return;
            }
        }

        var links = FindLinks.find(cacheData, objectLocations, componentGrid, tileWorld);
        var linkStore = new LinkStore(storeMeta, links);
        try {
            linkStore.save(outDir);
        } catch (IOException e) {
            log.debug("writing links failed", e);
            System.err.println("writing links failed");
            System.exit(1);
            return;
        }

        if (cmd.hasOption(skipGraphOpt)) {
            log.info("Skipping graph");
            return;
        }

        var tilePathfinder = tileWorld.toPathfinder(contiguousComponents.planes());
        var gridStore = new GridStore(storeMeta, tilePathfinder.grid());
        try {
            gridStore.save(outDir);
        } catch (IOException e) {
            log.debug("writing grid failed", e);
            System.err.println("writing grid failed");
            System.exit(1);
            return;
        }

        var linkedComponents = LinkedComponents.create(contiguousComponents, links);
        var componentGraph = CreateComponentGraph.create(linkedComponents, tilePathfinder);

        var graphStore = new GraphStore(storeMeta, contiguousComponents.planes(), componentGraph);
        try {
            graphStore.save(outDir);
        } catch (IOException e) {
            log.debug("writing graph failed", e);
            System.err.println("writing graph failed");
            System.exit(1);
            return;
        }

        var time = (System.currentTimeMillis() - startTime) / 1000;
        log.info("finished in {}s", time);
    }
}
