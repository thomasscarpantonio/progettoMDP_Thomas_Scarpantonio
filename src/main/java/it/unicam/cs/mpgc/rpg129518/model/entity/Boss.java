package it.unicam.cs.mpgc.rpg129518.model.entity;

/** Boss di fine dungeon: ogni terzo turno scatena un attacco a danno doppio. */
public class Boss extends Monster {

    private static final int SPECIAL_EVERY_N_TURNS = 3;

    public Boss(String id, String name, int maxHp, int attack, int defense, int xpReward, int goldReward) {
        super(id, name, maxHp, attack, defense, xpReward, goldReward);
    }

    @Override
    public int attackForTurn(int turn) {
        return isSpecialTurn(turn) ? getAttack() * 2 : getAttack();
    }

    @Override
    public boolean isSpecialTurn(int turn) {
        return turn % SPECIAL_EVERY_N_TURNS == 0;
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}
