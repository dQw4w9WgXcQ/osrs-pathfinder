package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.Links;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record LinkStore(Links links) {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Requirement.class, new RequirementTypeAdapter())
            .create();

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

    @SuppressWarnings("unused")
    public static LinkStore load(File file) throws IOException {
        log.info("loading links from {}", file);

        Links links;
        try (var zis = new ZipInputStream(new FileInputStream(file))) {
            zis.getNextEntry();
            links = GSON.fromJson(new InputStreamReader(zis), Links.class);
        }

        return new LinkStore(links);
    }
}
