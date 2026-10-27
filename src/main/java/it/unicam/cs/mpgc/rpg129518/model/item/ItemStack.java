package it.unicam.cs.mpgc.rpg129518.model.item;

/** Coppia oggetto/quantità, vista di sola lettura di una voce dell'inventario. */
public record ItemStack(Item item, int quantity) {
}
