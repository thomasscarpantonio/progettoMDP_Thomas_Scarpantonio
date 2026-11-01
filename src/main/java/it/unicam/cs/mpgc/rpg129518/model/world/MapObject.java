package it.unicam.cs.mpgc.rpg129518.model.world;

import java.util.Optional;

/**
 * Oggetto posato su una casella. Gli oggetti dei dungeon hanno un {@link Room} che descrive cosa accade
 * quando il giocatore ci interagisce; quelli del villaggio sono semplici punti di interesse.
 */
public class MapObject {

    private final Position position;
    private final ObjectType type;
    private final Room encounter;
    private final String refId;
    private final String label;
    private boolean active = true;

    private MapObject(Position position, ObjectType type, Room encounter, String refId, String label) {
        this.position = position;
        this.type = type;
        this.encounter = encounter;
        this.refId = refId;
        this.label = label;
    }

    /** Oggetto di dungeon. refId = id del mostro (per scegliere lo sprite), se presente. */
    public static MapObject encounter(Position position, ObjectType type, Room room, String refId) {
        return new MapObject(position, type, room, refId, "");
    }

    /** Punto di interesse del villaggio. refId = id del dungeon per i cancelli. */
    public static MapObject landmark(Position position, ObjectType type, String refId, String label) {
        return new MapObject(position, type, null, refId, label);
    }

    public Position getPosition() {
        return position;
    }

    public ObjectType getType() {
        return type;
    }

    public Optional<Room> getEncounter() {
        return Optional.ofNullable(encounter);
    }

    public String getRefId() {
        return refId;
    }

    public String getLabel() {
        return label;
    }

    public boolean isActive() {
        return active;
    }

    /** Segna l'oggetto come "consumato" (mostro sconfitto, scrigno aperto, trappola scattata...). */
    public void deactivate() {
        active = false;
    }

    /** Un oggetto bloccante non si può attraversare: ci si "urta" contro per interagire. */
    public boolean isBlocking() {
        return switch (type) {
            case ENEMY, BOSS -> active;
            case INN, SHOP, SAVE_POINT, GATE -> true;
            case CHEST, FOUNTAIN, TRAP -> false;
        };
    }
}
