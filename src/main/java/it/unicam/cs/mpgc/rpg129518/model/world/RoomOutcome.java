package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;

import java.util.List;
import java.util.Optional;

/** Esito dell'ingresso in una stanza: messaggi ed eventuale mostro da combattere. */
public record RoomOutcome(List<String> messages, Monster monster) {

    public static RoomOutcome peaceful(String... messages) {
        return new RoomOutcome(List.of(messages), null);
    }

    public static RoomOutcome encounter(Monster monster, String... messages) {
        return new RoomOutcome(List.of(messages), monster);
    }

    public Optional<Monster> encounter() {
        return Optional.ofNullable(monster);
    }
}