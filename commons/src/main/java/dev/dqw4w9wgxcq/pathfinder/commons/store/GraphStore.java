package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.GsonBuilder;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGraph;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.Links;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record GraphStore(int[][][] componentGrid, ComponentGraph componentGraph) {
    public void save(File dir) throws IOException {
        log.info("saving graph to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        var graphGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(Link.class, new LinkTypeAdapter(null))
                .create();
        try (var fos = new FileOutputStream(new File(dir, "graph.zip"));
             var zos = new ZipOutputStream(fos)) {
            log.info("writing componentgrid.dat");
            zos.putNextEntry(new ZipEntry("componentgrid.dat"));
            try (var oos = new ObjectOutputStream(zos)) {
                oos.writeObject(componentGrid);

                log.info("writing componentgraph.json");
                zos.putNextEntry(new ZipEntry("componentgraph.json"));
                zos.write(graphGson.toJson(componentGraph).getBytes());
            }
        }
    }

    @SneakyThrows(ClassNotFoundException.class)
    public static GraphStore load(InputStream is, Links links) throws IOException {
        log.info("loading graph from {}", is);

        int[][][] componentGrid;
        ComponentGraph componentGraph;

        var graphGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Link.class, new LinkTypeAdapter(links))
                .create();

        try (is;
             var zis = new ZipInputStream(is)) {
            zis.getNextEntry();
            try (var ois = new ObjectInputStream(zis)) {
                componentGrid = (int[][][]) ois.readObject();

                zis.getNextEntry();
                componentGraph = graphGson.fromJson(new InputStreamReader(zis), ComponentGraph.class);
            }
        }

        return new GraphStore(componentGrid, componentGraph);
    }
}