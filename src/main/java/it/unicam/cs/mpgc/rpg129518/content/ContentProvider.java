package it.unicam.cs.mpgc.rpg129518.content;

/** Sorgente dei contenuti di gioco. Oggi JSON da risorse; domani potrebbe essere un server o un database. */
public interface ContentProvider {

    GameContent load();
}
