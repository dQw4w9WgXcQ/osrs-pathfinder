package dev.dqw4w9wgxcq.pathfinder.commons.store.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.LinkType;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.Links;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * For (de)serializing graph.  Uses an existing Link in Links instead of creating new instances.
 *
 * @param links can be null if only writing.
 */
@SuppressWarnings("JavadocReference")
@RequiredArgsConstructor
public class LinkTypeAdapter extends TypeAdapter<Link> {
    private final @Nullable Links links;

    @Override
    public void write(JsonWriter writer, Link value) throws IOException {
        writer.beginObject();
        writer.name("id");
        writer.value(value.id());
        writer.name("type");
        writer.value(LinkType.of(value).name());
        writer.endObject();
    }

    @Override
    public Link read(JsonReader reader) throws IOException {
        if (links == null) {
            throw new IllegalStateException("links is null, cannot read");
        }

        reader.beginObject();
        Integer id = null;
        LinkType type = null;

        while (reader.hasNext()) {
            var token = reader.peek();
            if (token.equals(JsonToken.NAME)) {
                var entryName = reader.nextName();
                if (entryName.equals("id")) {
                    id = reader.nextInt();
                } else if (entryName.equals("type")) {
                    type = LinkType.valueOf(reader.nextString());
                }
            }
        }
        reader.endObject();

        assert id != null;
        assert type != null;

        return links.getLink(type, id);
    }
}
