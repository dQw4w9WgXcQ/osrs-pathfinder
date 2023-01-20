package github.dqw4w9wgxcq.pathfinder.graph.store;

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
        ComponentGraph componentGraph
) {
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
            zos.putNextEntry(new ZipEntry("planes.dat"));
            try (var oos = new ObjectOutputStream(zos)) {
                oos.writeObject(planes);

                //need to write next entry before closing oos
                zos.putNextEntry(new ZipEntry("componentgraph.json"));
                zos.write(graphGson.toJson(componentGraph).getBytes());
            }
        }
    }

    @SneakyThrows(ClassNotFoundException.class)
    public static GraphStore load(File dir, Links links) throws IOException {
        log.info("loading graph from {}", dir);

        int[][][] grid;
        ComponentGraph componentGraph;

        var graphGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Link.class, new LinkTypeAdapter(links))
                .create();

        try (var fis = new FileInputStream(new File(dir, "graph.zip"));
             var zis = new ZipInputStream(fis)) {
            zis.getNextEntry();
            try (var ois = new ObjectInputStream(zis)) {
                grid = (int[][][]) ois.readObject();

                //need to read next entry before closing ois
                zis.getNextEntry();
                componentGraph = graphGson.fromJson(new InputStreamReader(zis), ComponentGraph.class);
            }
        }

        return new GraphStore(grid, componentGraph);
    }
}
