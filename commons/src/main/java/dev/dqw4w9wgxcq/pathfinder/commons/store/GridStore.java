package dev.dqw4w9wgxcq.pathfinder.commons.store;

import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

            log.debug("writing grid.dat");
            zos.putNextEntry(new ZipEntry("grid.dat"));
            try (var dos = new DataOutputStream(zos)) {
                dos.writeInt(grid[0].length);
                dos.writeInt(grid[0][0].length);
                for (var plane : grid) {
                    for (var col : plane) {
                        dos.write(col);
                    }
                }
            }
        }
    }

    public static GridStore load(File file) throws IOException {
        log.info("loading grid from {}", file);

        byte[][][] grid;
        try (var is = new FileInputStream(file);
             var zis = new ZipInputStream(is)) {
            log.debug("reading grid.dat");
            zis.getNextEntry();
            try (var dis = new DataInputStream(zis)) {
                var width = dis.readInt();
                var height = dis.readInt();

                grid = new byte[Constants.PLANES_SIZE][width][height];
                for (var plane = 0; plane < Constants.PLANES_SIZE; plane++) {
                    for (var x = 0; x < width; x++) {
                        dis.readFully(grid[plane][x]);
                    }
                }
            }
        }

        return new GridStore(grid);
    }
}
