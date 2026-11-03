package it.unicam.cs.mpgc.rpg129518.persistence;

import java.util.Optional;

/** Astrazione della persistenza: il gioco non sa se si salva su JSON, XML, database o cloud. */
public interface SaveRepository {

    void save(String slot,SaveData data);

    Optional<SaveData> load(String slot);

    boolean exists(String slot);

    void delete(String slot);
}
