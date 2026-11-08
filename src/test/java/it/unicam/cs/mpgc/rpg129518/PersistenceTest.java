package it.unicam.cs.mpgc.rpg129518;

import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.content.JsonContentProvider;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import it.unicam.cs.mpgc.rpg129518.model.item.Armor;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129518.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {

    private final GameContent content = new JsonContentProvider().load();

    private SaveData sampleSave() {
        Player player = new Player("Aria", PlayerClass.MAGE);
        player.equip((Weapon) content.item("iron_sword"));
        player.equip((Armor) content.item("leather_armor"));
        player.getInventory().add(content.item("small_potion"), 3);
        player.addGold(50);
        player.gainXp(25);
        return SaveMapper.toSaveData(player, List.of("crypt"));
    }

    @Test
    void jsonRepositoryRoundTrip(@TempDir Path dir) {
        roundTrip(new JsonSaveRepository(dir));
    }

    private void roundTrip(SaveRepository repository) {
        SaveData original = sampleSave();
        assertFalse(repository.exists("MAGE"));
        repository.save("MAGE",original);
        assertTrue(repository.exists("MAGE"));
        assertEquals(original, repository.load("MAGE").orElseThrow());
        repository.delete("MAGE");
        assertFalse(repository.exists("MAGE"));
    }

    @Test
    void mapperRestoresThePlayer() {
        Player restored = SaveMapper.toPlayer(sampleSave(), content);
        assertEquals("Aria", restored.getName());
        assertEquals(50, restored.getGold());
        assertEquals(2, restored.getLevel());
        assertEquals("iron_sword", restored.getWeapon().orElseThrow().getId());
        assertEquals(3, restored.getInventory().count(content.item("small_potion")));
    }
}
