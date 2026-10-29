package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.item.Consumable;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scontro a turni tra giocatore e mostro. Ogni azione del giocatore consuma il turno
 * e (se il mostro è ancora vivo) fa seguire la risposta del mostro.
 * La classe non conosce l'interfaccia: restituisce solo i messaggi da mostrare.
 */
public class Combat {

    private static final double FLEE_CHANCE = 0.5;
    private static final int MANA_REGEN_PER_TURN = 1;

    private final Player player;
    private final Monster monster;
    private final DamageCalculator calculator;
    private final Random random;
    private int turn = 1;
    private boolean defending;
    private CombatState state = CombatState.ONGOING;

    public Combat(Player player, Monster monster, Random random) {
        this.player = player;
        this.monster = monster;
        this.random = random;
        this.calculator = new DamageCalculator(random);
    }

    public Monster getMonster() {
        return monster;
    }

    public CombatState getState() {
        return state;
    }

    public int getTurn() {
        return turn;
    }

    // ---- logica interna ----

    private boolean isOngoing(List<String> log) {
        if (state != CombatState.ONGOING) {
            log.add("Il combattimento è già concluso.");
            return false;
        }
        return true;
    }


    private void monsterTurn(List<String> log) {
        if (monster.isSpecialTurn(turn)) {
            log.add(monster.getName() + " scatena un attacco devastante!");
        }
        Hit hit = calculator.rollHit(monster.attackForTurn(turn), player.getDefense());
        int damage = defending ? Math.max(1, hit.amount() / 2) : hit.amount();
        int dealt = player.takeDamage(damage);
        log.add(monster.getName() + " colpisce " + player.getName() + " per " + dealt + " danni"
                + (defending ? " (parati a metà)" : "") + ".");
        defending = false;
    }

}
