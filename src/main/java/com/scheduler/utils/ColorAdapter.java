package com.scheduler.utils;

import com.google.gson.*;
import java.awt.Color;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("r", color.getRed());
        obj.addProperty("g", color.getGreen());
        obj.addProperty("b", color.getBlue());
        return obj;
    }

    @Override
    public Color deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int r = obj.get("r").getAsInt();
        int g = obj.get("g").getAsInt();
        int b = obj.get("b").getAsInt();
        return new Color(r, g, b);
    }
}