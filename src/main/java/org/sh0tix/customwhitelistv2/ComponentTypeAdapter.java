package org.sh0tix.customwhitelistv2;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class ComponentTypeAdapter extends TypeAdapter<Component> {

    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String jsonStr = GsonComponentSerializer.gson().serialize(value);
        JsonElement jsonElement = new JsonParser().parse(jsonStr);
        new Gson().toJson(jsonElement, out);
    }
    
    @Override
    public Component read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
            JsonElement jsonElement = new JsonParser().parse(in);
            return GsonComponentSerializer.gson().deserialize(jsonElement.toString());
        } else {
            return GsonComponentSerializer.gson().deserialize(in.nextString());
        }
    }
}
