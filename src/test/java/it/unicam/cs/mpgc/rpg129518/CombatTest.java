package it.unicam.cs.mpgc.rpg129518;

import it.unicam.cs.mpgc.rpg129518.model.combat.AbilityCatalog;
import it.unicam.cs.mpgc.rpg129518.model.combat.Combat;
import it.unicam.cs.mpgc.rpg129518.model.combat.CombatState;
import it.unicam.cs.mpgc.rpg129518.model.entity.Boss;
import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatTest {

    @Test
    void defeatingAMonsterRewardsGoldAndXp() {
        Player player = new Player("Test", PlayerClass.WARRIOR);
        Monster rat = new Monster("rat", "Ratto", 1, 1, 0, 10, 5);
        Combat combat = new Combat(player, rat, new Random(1));

        combat.attack();

        assertEquals(CombatState.VICTORY, combat.getState());
        assertEquals(5, player.getGold());
        assertEquals(10, player.getXp());
    }

    @Test
    void enoughExperienceMakesThePlayerLevelUp() {
        Player player = new Player("Test", PlayerClass.MAGE);
        int levels = player.gainXp(20);
        assertEquals(1, levels);
        assertEquals(2, player.getLevel());
        assertEquals(player.getMaxHp(), player.getHp());
    }

    @Test
    void bossUsesDoubleAttackEveryThirdTurn() {
        Boss boss = new Boss("b", "Boss", 50, 10, 0, 0, 0);
        assertEquals(10, boss.attackForTurn(1));
        assertEquals(20, boss.attackForTurn(3));
    }

    @Test
    void cannotFleeFromABoss() {
        Player player = new Player("Test", PlayerClass.ROGUE);
        Boss boss = new Boss("b", "Boss", 500, 1, 0, 0, 0);
        Combat combat = new Combat(player, boss, new Random(1));
        combat.flee();
        assertEquals(CombatState.ONGOING, combat.getState());
        assertEquals(1, combat.getTurn());
    }

    @Test
    void abilityNeedsEnoughMana() {
        Player player = new Player("Test", PlayerClass.WARRIOR);
        Monster dummy = new Monster("d", "Manichino", 100, 1, 0, 0, 0);
        Combat combat = new Combat(player, dummy, new Random(1));
        while (player.getMana() >= AbilityCatalog.forClass(player.getPlayerClass()).getManaCost()) {
            combat.useAbility();
        }
        int hpBefore = dummy.getHp();
        combat.useAbility();
        assertEquals(hpBefore, dummy.getHp());
        assertTrue(player.isAlive());
    }
}
