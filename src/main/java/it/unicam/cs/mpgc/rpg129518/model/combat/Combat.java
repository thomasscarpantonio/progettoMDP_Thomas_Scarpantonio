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

    public List<String> attack() {
        List<String> log = new ArrayList<>();
        if (!isOngoing(log)) {
            return log;
        }
        Hit hit = calculator.rollHit(player.getAttack(), monster.getDefense());
        int dealt = monster.takeDamage(hit.amount());
        log.add(player.getName() + " attacca " + monster.getName() + " e infligge " + dealt + " danni"
                + (hit.critical() ? " (COLPO CRITICO!)" : "") + ".");
        return completeTurn(log);
    }

    public List<String> useAbility() {
        List<String> log = new ArrayList<>();
        if (!isOngoing(log)) {
            return log;
        }
        Ability ability = player.getPlayerClass().getAbility();
        if (!player.spendMana(ability.getManaCost())) {
            log.add("Mana insufficiente per " + ability.getName() + ".");
            return log;
        }
        int dealt = monster.takeDamage(ability.computeDamage(player, monster, calculator));
        log.add(player.getName() + " usa " + ability.getName() + " e infligge " + dealt + " danni!");
        return completeTurn(log);
    }

    public List<String> defend() {
        List<String> log = new ArrayList<>();
        if (!isOngoing(log)) {
            return log;
        }
        defending = true;
        log.add(player.getName() + " si mette in guardia.");
        return completeTurn(log);
    }

    public List<String> useItem(Item item) {
        List<String> log = new ArrayList<>();
        if (!isOngoing(log)) {
            return log;
        }
        if (!(item instanceof Consumable consumable)) {
            log.add(item.getName() + " non si può usare in combattimento.");
            return log;
        }
        if (!player.getInventory().remove(item)) {
            log.add("Non possiedi " + item.getName() + ".");
            return log;
        }
        log.add(consumable.consume(player));
        return completeTurn(log);
    }

    public List<String> flee() {
        List<String> log = new ArrayList<>();
        if (!isOngoing(log)) {
            return log;
        }
        if (monster.isBoss()) {
            log.add("Non puoi fuggire da un boss!");
            return log;
        }
        if (random.nextDouble() < FLEE_CHANCE) {
            state = CombatState.FLED;
            return log;
        }
        log.add(player.getName() + " tenta la fuga, ma fallisce!");
        return completeTurn(log);
    }

    // ---- logica interna ----

    private boolean isOngoing(List<String> log) {
        if (state != CombatState.ONGOING) {
            log.add("Il combattimento è già concluso.");
            return false;
        }
        return true;
    }

    private List<String> completeTurn(List<String> log) {
        if (!monster.isAlive()) {
            resolveVictory(log);
            return log;
        }
        monsterTurn(log);
        if (!player.isAlive()) {
            state = CombatState.DEFEAT;
            log.add(player.getName() + " è stato sconfitto...");
        } else {
            player.restoreMana(MANA_REGEN_PER_TURN);
            turn++;
        }
        return log;
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

    private void resolveVictory(List<String> log) {
        state = CombatState.VICTORY;
        log.add(monster.getName() + " è stato sconfitto!");
        player.addGold(monster.getGoldReward());
        int levels = player.gainXp(monster.getXpReward());
        log.add("Ottieni " + monster.getXpReward() + " XP e " + monster.getGoldReward() + " monete d'oro.");
        if (levels > 0) {
            log.add("LIVELLO SU! Ora sei al livello " + player.getLevel() + ".");
        }
    }
}
