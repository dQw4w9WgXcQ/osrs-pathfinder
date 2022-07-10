package scratchpaper;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.Main;

import java.io.File;
import java.io.IOException;

/*
only obj with doororwall = 9 and name not null:
Nothing
13141
 */
public class Efefe {
    public static void main(String[] args) throws IOException {
        System.out.println(1 << 24);
        if (true)return;
        var data = new CacheData(new File(Main.DESKTOP_DIR, "cache"), new File(Main.DESKTOP_DIR, "xteas.json"));

        var objects = data.getObjectManager().getObjects();
        for (var object : objects) {
            if (object.getWallOrDoor() == 0 && !object.getName().equals("null")) {
                System.out.println(object.getName());
                System.out.println(object.getId());
            }
        }
    }
}
