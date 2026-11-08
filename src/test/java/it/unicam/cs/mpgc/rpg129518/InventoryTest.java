package it.unicam.cs.mpgc.rpg129518;

import it.unicam.cs.mpgc.rpg129518.model.item.Inventory;
import it.unicam.cs.mpgc.rpg129518.model.item.Potion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void itemsWithSameIdAreStackedTogether() {
        Inventory inventory = new Inventory();
        Potion a = new Potion("p", "Pozione", "", 10, 15, 0);
        Potion b = new Potion("p", "Pozione", "", 10, 15, 0);
        inventory.add(a);
        inventory.add(b);
        assertEquals(2, inventory.count(a));
        assertEquals(1, inventory.getStacks().size());
    }

    @Test
    void removeDecrementsAndEventuallyEmptiesTheStack() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion("p", "Pozione", "", 10, 15, 0);
        inventory.add(potion, 2);
        assertTrue(inventory.remove(potion));
        assertTrue(inventory.remove(potion));
        assertFalse(inventory.remove(potion));
        assertTrue(inventory.isEmpty());
    }
}
