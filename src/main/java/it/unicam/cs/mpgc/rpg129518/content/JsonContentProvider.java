package it.unicam.cs.mpgc.rpg129518.content;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Carica i contenuti dai file JSON presenti nelle risorse (items.json, monsters.json, dungeons.json). */
public class JsonContentProvider implements ContentProvider {

    private final Gson gson = new Gson();

    @Override
    public GameContent load() {
        return new GameContent(
                read("/items.json", ItemDefinition[].class),
                read("/monsters.json", MonsterTemplate[].class),
                read("/dungeons.json", DungeonDefinition[].class),
                readLines("/village.txt"));
    }

    private List<String> readLines(String resource) {
        try (InputStream in = JsonContentProvider.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Risorsa mancante: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile leggere " + resource, e);
        }
    }

    private <T> List<T> read(String resource, Class<T[]> arrayType) {
        try (InputStream in = JsonContentProvider.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Risorsa mancante: " + resource);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return List.of(gson.fromJson(reader, arrayType));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile leggere " + resource, e);
        }
    }
}
