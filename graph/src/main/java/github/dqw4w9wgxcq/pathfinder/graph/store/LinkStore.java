package github.dqw4w9wgxcq.pathfinder.graph.store;

import com.google.gson.Gson;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Links;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public record LinkStore(Links links) {
    private static final Gson GSON = new Gson();

    public void save(File dir) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "links.zip"));
             var zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("links.json"));
            zos.write(GSON.toJson(links).getBytes());
        }
    }

    public static LinkStore load(File dir) throws IOException {
        Links links;

        try (var fis = new FileInputStream(new File(dir, "links.zip"));
             var zis = new ZipInputStream(fis)) {
            zis.getNextEntry();
            links = GSON.fromJson(new InputStreamReader(zis), Links.class);
        }

        return new LinkStore(links);
    }
}
