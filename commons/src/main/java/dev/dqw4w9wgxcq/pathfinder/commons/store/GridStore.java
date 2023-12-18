package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.Gson;
import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            log.debug("writing meta.json");
            zos.putNextEntry(new ZipEntry("meta.json"));
            zos.write(GSON.toJson(new Meta(grid[0].length, grid[0][0].length)).getBytes());

            log.debug("writing grid.dat");
            zos.putNextEntry(new ZipEntry("grid.dat"));
            try (var dos = new DataOutputStream(zos)) {
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
            log.debug("reading meta.json");
            zis.getNextEntry();
            var meta = GSON.fromJson(new InputStreamReader(zis), Meta.class);
            log.debug("meta: {}", meta);

            log.debug("reading grid.dat");
            zis.getNextEntry();
            try (var dis = new DataInputStream(zis)) {
                grid = new byte[Constants.PLANES_SIZE][meta.width][meta.height];
                for (var plane = 0; plane < Constants.PLANES_SIZE; plane++) {
                    for (var x = 0; x < meta.width; x++) {
                        dis.readFully(grid[plane][x]);
                    }
                }
            }
        }

        return new GridStore(grid);
    }

    public static void main(String[] args) throws IOException {
        var dir = new File(System.getProperty("user.dir"), "asdf");

        var grid = new byte[Constants.PLANES_SIZE][10][15];

        grid[0][0][0] = 1;
        grid[0][0][1] = 3;
        grid[0][0][2] = 5;
        grid[1][3][3] = 7;
        grid[0][3][4] = (byte) (1 << 7);

        new GridStore(grid).save(dir);

        var loaded = GridStore.load(new File(dir, "grid.zip"));

        System.out.println(loaded.grid[0][0][0]);
        System.out.println(loaded.grid[0][0][1]);
        System.out.println(loaded.grid[0][0][2]);
        System.out.println(loaded.grid[1][3][3]);
        System.out.println(loaded.grid[0][3][4]);
    }
}
