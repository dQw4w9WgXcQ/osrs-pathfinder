package dev.dqw4w9wgxcq.pathfinder.pathfinding.store;

import com.google.gson.Gson;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.domain.Links;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record LinkStore(Links links) {
    private static final Gson GSON = new Gson();

    public void save(File dir) throws IOException {
        log.info("saving links to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "links.zip"));
             var zos = new ZipOutputStream(fos)) {
            zos.putNextEntry(new ZipEntry("links.json"));
            zos.write(GSON.toJson(links).getBytes());
        }
    }

    public static LinkStore load(InputStream is) throws IOException {
        log.info("loading links from {}", is);

        Links links;
        try (is;
             var zis = new ZipInputStream(is)) {
            zis.getNextEntry();
            links = GSON.fromJson(new InputStreamReader(zis), Links.class);
        }

        return new LinkStore(links);
    }
}
