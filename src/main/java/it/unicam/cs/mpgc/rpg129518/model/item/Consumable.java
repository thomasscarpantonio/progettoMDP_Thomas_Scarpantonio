package it.unicam.cs.mpgc.rpg129518.model.item;


/** Oggetto che si consuma quando viene usato. */
public interface Consumable {

    /** Applica l'effetto al giocatore e restituisce un messaggio per il log. */
    String consume(ItemUser user);
}
