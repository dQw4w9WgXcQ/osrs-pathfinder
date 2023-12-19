package dev.dqw4w9wgxcq.pathfinder.commons.store;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

public class RequirementTypeAdapter extends TypeAdapter<Requirement> {
    private static final Gson GSON = new Gson();

    @Override
    public void write(JsonWriter out, Requirement value) {
        GSON.toJson(value, value.getClass(), out);
    }

    @Override
    public Requirement read(JsonReader in) {
        var json = JsonParser.parseReader(in).getAsJsonObject();
        var type = json.get("type").getAsString();
        return GSON.fromJson(json, Requirement.Type.valueOf(type).getClazz());
    }
}
