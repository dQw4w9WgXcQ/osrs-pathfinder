package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.Gson;
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
public record GridStore(byte[][][] grid) {
    private record Meta(int width, int height) {
    }

    private static final Gson GSON = new Gson();

    public void save(File dir) throws IOException {
        log.info("saving grid to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "grid.zip"));
             var zos = new ZipOutputStream(fos)) {
            log.info("writing meta.json");
            zos.putNextEntry(new ZipEntry("meta.json"));
            zos.write(GSON.toJson(new Meta(grid[0].length, grid[0][0].length)).getBytes());

            zos.putNextEntry(new ZipEntry("grid.dat"));
            try (var oos = new ObjectOutputStream(zos)) {
                log.info("writing grid.dat");
                oos.writeObject(grid);
            }
        }
    }

    @SneakyThrows(ClassNotFoundException.class)
    public static GridStore load(InputStream is) throws IOException {
        log.info("loading grid from {}", is);

        byte[][][] grid;
        try (is;
             var zis = new ZipInputStream(is)) {
            log.info("reading meta.json");
            zis.getNextEntry();
            var meta = GSON.fromJson(new InputStreamReader(zis), Meta.class);
            log.debug("meta: {}", meta);

            log.info("reading grid.dat");
            zis.getNextEntry();
            try (var ois = new ObjectInputStream(zis)) {
                grid = (byte[][][]) ois.readObject();
            }
        }

        return new GridStore(grid);
    }
}
