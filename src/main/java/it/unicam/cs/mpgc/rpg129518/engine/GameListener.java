package it.unicam.cs.mpgc.rpg129518.engine;

/**
 * Observer del gioco. Qualsiasi interfaccia (JavaFX oggi, mobile o web domani)
 * implementa questa interfaccia e si registra su {@link Game}.
 */
public interface GameListener {

    /** Un nuovo messaggio narrativo da mostrare al giocatore. */
    void onMessage(String message);

    /** Lo stato del gioco è cambiato: l'interfaccia deve ridisegnarsi. */
    void onStateChanged();

    /** Il giocatore è entrato nel negozio: l'interfaccia può aprire la schermata di acquisto. */
    default void onShopRequested() {
    }
}
