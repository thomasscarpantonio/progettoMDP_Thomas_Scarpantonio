package it.unicam.cs.mpgc.rpg129518.persistence;

import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import it.unicam.cs.mpgc.rpg129518.model.item.Armor;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import it.unicam.cs.mpgc.rpg129518.model.item.ItemStack;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Converte tra oggetti di gioco e {@link SaveData}, così le classi di dominio restano ignare della persistenza. */
public final class SaveMapper {

    private SaveMapper() {
    }

    public static SaveData toSaveData(Player player, Collection<String> completedDungeons) {
        Map<String, Integer> inventory = new LinkedHashMap<>();
        for (ItemStack stack : player.getInventory().getStacks()) {
            inventory.put(stack.item().getId(), stack.quantity());
        }
        return new SaveData(
                player.getName(),
                player.getPlayerClass().name(),
                player.getLevel(),
                player.getXp(),
                player.getHp(),
                player.getMana(),
                player.getGold(),
                inventory,
                player.getWeapon().map(Item::getId).orElse(null),
                player.getArmor().map(Item::getId).orElse(null),
                List.copyOf(completedDungeons));
    }

    public static Player toPlayer(SaveData data, GameContent content) {
        Player player = new Player(data.playerName(), PlayerClass.valueOf(data.playerClass()));
        player.restoreState(data.level(), data.xp(), data.hp(), data.mana(), data.gold());
        data.inventory().forEach((id, quantity) -> player.getInventory().add(content.item(id), quantity));
        if (data.weaponId() != null && content.item(data.weaponId()) instanceof Weapon weapon) {
            player.equip(weapon);
        }
        if (data.armorId() != null && content.item(data.armorId()) instanceof Armor armor) {
            player.equip(armor);
        }
        return player;
    }
}
