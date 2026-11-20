package it.unicam.cs.mpgc.rpg129518.model.entity;

import it.unicam.cs.mpgc.rpg129518.model.item.Armor;
import it.unicam.cs.mpgc.rpg129518.model.item.Inventory;
import it.unicam.cs.mpgc.rpg129518.model.item.ItemUser;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;

import java.util.Optional;

/**
 * L'eroe controllato dall'utente. Le statistiche dipendono da classe e livello,
 * quindi per ripristinare un salvataggio bastano pochi valori ({@link #restoreState}).
 */
  public final class Player extends Entity implements ItemUser
  {
      private static final int HP_PER_LEVEL = 6;
      private static final int MANA_PER_LEVEL = 3;
      private static final int XP_PER_LEVEL = 20;

      private final PlayerClass playerClass;
      private final Inventory inventory = new Inventory();
      private int level = 1;
      private int xp;
      private int mana;
      private int gold;
      private Weapon weapon;
      private Armor armor;

      public Player(String name, PlayerClass playerClass) {
          super(name);
          this.playerClass = playerClass;
          this.hp = getMaxHp();
          this.mana = getMaxMana();
      }

      // ---- statistiche derivate ----

      @Override
      public int getMaxHp() {
          return playerClass.getBaseHp() + HP_PER_LEVEL * (level - 1);
      }

      public int getMaxMana() {
          return playerClass.getBaseMana() + MANA_PER_LEVEL * (level - 1);
      }

      @Override
      public int getBaseAttack() {
          return playerClass.getBaseAttack() + (level - 1);
      }

      @Override
      public int getBaseDefense() {
          return playerClass.getBaseDefense() + (level - 1) / 2;
      }

      @Override
      public int getAttack() {
          return getBaseAttack() + (weapon == null ? 0 : weapon.getAttackBonus());
      }

      @Override
      public int getDefense() {
          return getBaseDefense() + (armor == null ? 0 : armor.getDefenseBonus());
      }

      // ---- esperienza ----

      public int xpToNextLevel() {
          return XP_PER_LEVEL * level;
      }

      /** @return il numero di livelli guadagnati. */
      public int gainXp(int amount) {
          xp += Math.max(0, amount);
          int levelsGained = 0;
          while (xp >= xpToNextLevel()) {
              xp -= xpToNextLevel();
              level++;
              levelsGained++;
              hp = getMaxHp();
              mana = getMaxMana();
          }
          return levelsGained;
      }

      // ---- mana ----

      public boolean spendMana(int amount) {
          if (amount > mana) {
              return false;
          }
          mana -= amount;
          return true;
      }

      /** @return il mana realmente recuperato. */
      public int restoreMana(int amount) {
          int before = mana;
          mana = Math.min(getMaxMana(), mana + Math.max(0, amount));
          return mana - before;
      }

      public void restoreFully() {
          hp = getMaxHp();
          mana = getMaxMana();
      }

      // ---- oro ----

      public void addGold(int amount) {
          gold += Math.max(0, amount);
      }

      public boolean spendGold(int amount) {
          if (amount > gold) {
              return false;
          }
          gold -= amount;
          return true;
      }

      // ---- equipaggiamento ----

      /** Equipaggia l'arma; quella precedente torna nello zaino. */
      public void equip(Weapon newWeapon) {
          Weapon old = weapon;
          weapon = newWeapon;
          if (old != null) {
              inventory.add(old);
          }
      }

      /** Equipaggia l'armatura; quella precedente torna nello zaino. */
      public void equip(Armor newArmor) {
          Armor old = armor;
          armor = newArmor;
          if (old != null) {
              inventory.add(old);
          }
      }

      public Optional<Weapon> getWeapon() {
          return Optional.ofNullable(weapon);
      }

      public Optional<Armor> getArmor() {
          return Optional.ofNullable(armor);
      }

      /** Usato dal caricamento di un salvataggio. */
      public void restoreState(int level, int xp, int hp, int mana, int gold) {
          this.level = Math.max(1, level);
          this.xp = Math.max(0, xp);
          this.gold = Math.max(0, gold);
          this.hp = Math.min(Math.max(1, hp), getMaxHp());
          this.mana = Math.min(Math.max(0, mana), getMaxMana());
      }

      // ---- getter ----

      public PlayerClass getPlayerClass() {
          return playerClass;
      }

      public Inventory getInventory() {
          return inventory;
      }

      public int getLevel() {
          return level;
      }

      public int getXp() {
          return xp;
      }

      public int getMana() {
          return mana;
      }

      public int getGold() {
          return gold;
      }
  }
