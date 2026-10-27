package it.unicam.cs.mpgc.rpg129518.model.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Zaino del giocatore: mappa oggetto -> quantità (si appoggia su equals/hashCode di {@link Item}). */
public class Inventory {

    private final Map<Item, Integer> items = new LinkedHashMap<>();

    public void add(Item item) {
        add(item, 1);
    }

    public void add(Item item, int quantity) {
        if (quantity > 0) {
            items.merge(item, quantity, Integer::sum);
        }
    }

    /** @return true se l'oggetto era presente ed è stato rimosso (una copia). */
    public boolean remove(Item item) {
        Integer count = items.get(item);
        if (count == null) {
            return false;
        }
        if (count == 1) {
            items.remove(item);
        } else {
            items.put(item, count - 1);
        }
        return true;
    }

    public int count(Item item) {
        return items.getOrDefault(item, 0);
    }

    public Optional<Item> findById(String id) {
        return items.keySet().stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    public List<ItemStack> getStacks() {
        return items.entrySet().stream()
                .map(entry -> new ItemStack(entry.getKey(), entry.getValue()))
                .toList();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
