package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridWorld;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public record Graph() {
    public static Graph generate(CacheData data) {
        var gridWorld = GridWorld.create(data.regionData(), data.objectData());

        var componentsPlanes = ContiguousComponents.findIn(gridWorld);

        var plane0Components = componentsPlanes.get(0);

        try (var w = new JsonWriter(new FileWriter(new File(Main.DESKTOP_DIR, "components.json")))) {
            new Gson().toJson(componentsPlanes, new TypeToken<List<ContiguousComponents>>() {
            }.getType(), w);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //var doorLinks = DoorLinks.find(data.regionData(), data.objectData());

        //PlaneLinks.find(data.regionData(), data.objectData());

        return new Graph();
    }
}
