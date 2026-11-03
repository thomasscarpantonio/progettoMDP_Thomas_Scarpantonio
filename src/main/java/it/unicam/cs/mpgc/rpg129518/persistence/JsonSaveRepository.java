package it.unicam.cs.mpgc.rpg129518.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/** Salvataggio su file JSON. */
public class JsonSaveRepository implements SaveRepository {

    private final Path directory;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonSaveRepository(Path directory) {
        this.directory = directory;
    }

    private Path fileFor(String slot) {
        return directory.resolve("save_" + slot.toLowerCase() + ".json");
    }

    @Override
    public void save(String slot, SaveData data) {
        try {
            Files.createDirectories(directory);
            Files.writeString(fileFor(slot), gson.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenceException("Impossibile salvare la partita", e);
        }
    }

    @Override
    public Optional<SaveData> load(String slot) {
        if (!exists(slot)) {
            return Optional.empty();
        }
        try {
            String json = Files.readString(fileFor(slot), StandardCharsets.UTF_8);
            return Optional.ofNullable(gson.fromJson(json, SaveData.class));
        } catch (IOException | JsonParseException e) {
            throw new PersistenceException("Impossibile leggere il salvataggio", e);
        }
    }

    @Override
    public boolean exists(String slot) {
        return Files.isRegularFile(fileFor(slot));
    }

    @Override
    public void delete(String slot) {
        try {
            Files.deleteIfExists(fileFor(slot));
        } catch (IOException e) {
            throw new PersistenceException("Impossibile eliminare il salvataggio", e);
        }
    }
}