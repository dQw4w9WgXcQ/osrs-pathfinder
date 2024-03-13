package dev.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.util.XteaKey;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertXteas {
    public static void main(String[] args) throws Exception {
        var file = new File("xteasopenrs2.json");
        if (!file.exists()) {
            System.err.println("xteasopenrs2.json does not exist");
            System.exit(1);
            return;
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();

        List<XteaOpenRS2Format> keys =
                gson.fromJson(new FileReader(file), new TypeToken<List<XteaOpenRS2Format>>() {}.getType());

        System.out.println("found " + keys.size() + " keys");

        var map = new ArrayList<>();
        for (var key : keys) {
            var rl = new XteaKey();
            rl.setRegion(key.mapsquare());
            rl.setKeys(key.key());
            map.add(rl);
        }

        var json = gson.toJson(map);

        var writer = new BufferedWriter(new FileWriter("xteas.json"));
        writer.write(json);
        writer.close();

        System.out.println("wrote " + map.size() + " keys");
    }

    @SuppressWarnings("unused")
    private record XteaOpenRS2Format(int mapsquare, int[] key) {}
}
