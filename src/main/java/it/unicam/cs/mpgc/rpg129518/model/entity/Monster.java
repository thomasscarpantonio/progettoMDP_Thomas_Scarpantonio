package it.unicam.cs.mpgc.rpg129518.model.entity;

/** Nemico comune. I nemici speciali (es. {@link Boss}) ne estendono il comportamento. */
public class Monster extends Entity {

    private final String id;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int xpReward;
    private final int goldReward;

    public Monster(String id, String name, int maxHp, int attack, int defense, int xpReward, int goldReward) {
        super(name);
        this.id = id;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.xpReward = xpReward;
        this.goldReward = goldReward;
        this.hp = maxHp;
    }

    public String getId() {
        return id;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public int getBaseAttack() {
        return attack;
    }

    @Override
    public int getBaseDefense() {
        return defense;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    /** Potenza d'attacco del mostro al turno indicato (parte da 1). */
    public int attackForTurn(int turn) {
        return getAttack();
    }

    /** Indica se al turno indicato il mostro usa un attacco speciale. */
    public boolean isSpecialTurn(int turn) {
        return false;
    }

    public boolean isBoss() {
        return false;
    }
}
