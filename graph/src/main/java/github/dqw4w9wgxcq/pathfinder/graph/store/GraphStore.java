package github.dqw4w9wgxcq.pathfinder.graph.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Links;
import github.dqw4w9wgxcq.pathfinder.graph.store.gson.LinkTypeAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record GraphStore(
        int[][][] planes,
        ComponentGraph componentGraph,
        Links links
) {
    private static final Gson GSON = new Gson();
    private static final Gson GRAPH_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Link.class, new LinkTypeAdapter(null))
            .create();

    public void save(File dir) throws IOException {
        log.info("saving graph to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "graph.zip"))) {
            try (var zos = new ZipOutputStream(fos)) {
                zos.putNextEntry(new ZipEntry("planes.dat"));
                try (var oos = new ObjectOutputStream(zos)) {
                    oos.writeObject(planes);
                }

                zos.putNextEntry(new ZipEntry("componentgraph.json"));
                zos.write(GRAPH_GSON.toJson(componentGraph()).getBytes());
            }
        }

        var linksGson = new Gson();
        try (var fos = new FileOutputStream(new File(dir, "links.zip"));
             var zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("links.json"));
            zos.write(linksGson.toJson(links).getBytes());
        }
    }

    @SneakyThrows(ClassNotFoundException.class)
    public static GraphStore load(File dir) throws IOException {
        log.info("loading graph from {}", dir);

        int[][][] grid;
        ComponentGraph componentGraph;
        Links links;

        try (var fis = new FileInputStream(new File(dir, "graph.zip"));
             var zis = new ZipInputStream(fis)) {
            zis.getNextEntry();
            try (var ois = new ObjectInputStream(zis)) {
                grid = (int[][][]) ois.readObject();
            }

            zis.getNextEntry();
            componentGraph = GRAPH_GSON.fromJson(new InputStreamReader(zis), ComponentGraph.class);
        }

        try (var fis = new FileInputStream(new File(dir, "links.zip"));
             var zis = new ZipInputStream(fis)) {
            zis.getNextEntry();
            links = GSON.fromJson(new InputStreamReader(zis), Links.class);
        }

        return new GraphStore(grid, componentGraph, links);
    }
}
