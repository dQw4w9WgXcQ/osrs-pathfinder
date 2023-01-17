package github.dqw4w9wgxcq.pathfinder.graph.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.edge.LinkEdge;
import github.dqw4w9wgxcq.pathfinder.graph.Links;
import github.dqw4w9wgxcq.pathfinder.graph.store.gson.LinkTypeAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public record GraphStore(
        Map<Link, List<LinkEdge>> linkGraph,
        int[][][] components,
        Links links
) {
    public void save(File dir) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        var gson = new Gson();
        var graphGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Link.class, new LinkTypeAdapter(null))
                .create();

        try (var fos = new FileOutputStream(new File(dir, "graph.zip"))) {
            try (var zos = new ZipOutputStream(fos);
                 var oos = new ObjectOutputStream(zos)) {
                zos.putNextEntry(new ZipEntry("components.dat"));
                oos.writeObject(components);
            }

            try (var zos = new ZipOutputStream(fos)) {
                zos.putNextEntry(new ZipEntry("linkgraph.json"));
                zos.write(graphGson.toJson(linkGraph).getBytes());
            }
        }

        try (var fos = new FileOutputStream(new File(dir, "links.zip"))) {
            try (var zos = new ZipOutputStream(fos)) {
                zos.putNextEntry(new ZipEntry("links.json"));
                zos.write(gson.toJson(links).getBytes());
            }
        }
    }

//    public static GraphStore load(File dir) {
//
//    }
}
