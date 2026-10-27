package it.unicam.cs.mpgc.rpg129518.model.item;

import java.util.Objects;

/** Oggetto del gioco. L'identità è definita dall'id: due istanze con lo stesso id sono lo stesso oggetto. */
public abstract class Item {

    private final String id;
    private final String name;
    private final String description;
    private final int value;

    protected Item(String id, String name, String description, int value) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = description == null ? "" : description;
        this.value = value;
    }

    /** Breve descrizione degli effetti (es. "+3 attacco"), usata dall'interfaccia. */
    public abstract String getDetails();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Item other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
