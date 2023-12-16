package dev.dqw4w9wgxcq.pathfinder.commons.store;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record GridStore(byte[][][] grid) {
    public void save(File dir) throws IOException {
        log.info("saving grid to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "grid.zip"));
             var zos = new ZipOutputStream(fos)) {
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
            log.info("reading grid.dat");
            zis.getNextEntry();
            try (var ois = new ObjectInputStream(zis)) {
                grid = (byte[][][]) ois.readObject();
            }
        }

        return new GridStore(grid);
    }
}
