package it.unicam.cs.mpgc.rpg129518.model.item;


/** Oggetto che può essere equipaggiato dal giocatore. */
public interface Equippable {

    /** Equipaggia l'oggetto e restituisce un messaggio per il log. */
    String equipOn(ItemUser user);
}
