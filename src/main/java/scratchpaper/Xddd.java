package scratchpaper;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.CacheData;

import java.io.File;
import java.io.IOException;

public class Xddd {
    private static final File DESKTOP_DIR = new File(System.getProperty("user.home"), "Desktop");

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, World!");
        var cacheDir = new File(DESKTOP_DIR, "cache");
        var xteasFile = new File(DESKTOP_DIR, "xteas.json");

        var cacheData = new CacheData(cacheDir, xteasFile);

        var id = 12850;
        var baseX = ((id >> 8) & 0xFF) << 6; // local coords are in bottom 6 bits (64*64)
        var baseY = (id & 0xFF) << 6;

        System.out.println("baseX: " + baseX);
        System.out.println("baseY: " + baseY);

        var region = cacheData.getRegionLoader().findRegionForWorldCoordinates(baseX, baseY);

        System.out.println("region: " + region.getRegionID());

        for (var x = -1; x < 64; x++) {
            for (var y = -1; y < 64; y++) {
                if (x == -1) {
                    if (y == -1) {
                        System.out.print("   ");
                    } else {
                        System.out.print(y);
                        if (y < 9) {
                            System.out.print("  ");
                        } else {
                            System.out.print(" ");
                        }
                    }
                    continue;
                }

                if (y == -1) {
                    System.out.print(x);
                    if (x < 10) {
                        System.out.print("  ");
                    } else {
                        System.out.print(" ");
                    }
                    continue;
                }

                var tileSetting = region.getTileSetting(0, x, y);
                System.out.print(tileSetting + "  ");
            }

            System.out.println();
        }
    }
}
