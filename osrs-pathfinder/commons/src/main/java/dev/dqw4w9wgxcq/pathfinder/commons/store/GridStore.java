package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.Gson;
import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public record GridStore(StoreMeta meta, byte[][][] grid) {
    public void save(File dir) throws IOException {
        log.info("saving grid to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (var fos = new FileOutputStream(new File(dir, "grid.zip"));
                var zos = new ZipOutputStream(fos)) {

            log.debug("writing meta.json");
            zos.putNextEntry(new ZipEntry("meta.json"));
            zos.write(new Gson().toJson(meta).getBytes());

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

    @SuppressWarnings("unused")
    public static GridStore load(File file) throws IOException {
        log.info("loading grid from {}", file);

        StoreMeta meta;
        byte[][][] grid;
        try (var is = new FileInputStream(file);
                var zis = new ZipInputStream(is)) {
            log.debug("reading meta.json");
            zis.getNextEntry();
            meta = new Gson().fromJson(new InputStreamReader(zis), StoreMeta.class);

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

        return new GridStore(meta, grid);
    }

    public static void main(String[] args) throws IOException {
        var dir = new File(new File(System.getProperty("user.home"), "Desktop"), "asdf");

        var grid = new byte[Constants.PLANES_SIZE][10][15];

        grid[0][0][0] = 1;
        grid[0][0][1] = 3;
        grid[0][0][2] = 5;
        grid[1][3][3] = 7;
        grid[0][3][4] = (byte) (1 << 7);
        grid[0][9][14] = 11;

        new GridStore(new StoreMeta("v", Instant.now(), "d"), grid).save(dir);

        var loaded = GridStore.load(new File(dir, "grid.zip"));

        System.out.println(loaded.grid[0][0][0]);
        System.out.println(loaded.grid[0][0][1]);
        System.out.println(loaded.grid[0][0][2]);
        System.out.println(loaded.grid[1][3][3]);
        System.out.println(loaded.grid[0][3][4]);
        System.out.println(loaded.grid[0][9][14]);

        System.out.println(Arrays.deepToString(loaded.grid()));

        System.out.println(loaded.meta());
    }
}
