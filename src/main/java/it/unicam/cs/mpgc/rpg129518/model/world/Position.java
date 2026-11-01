package it.unicam.cs.mpgc.rpg129518.model.world;

/** Coordinata di una casella (colonna x, riga y). Immutabile: usabile come chiave di HashMap. */
public record Position(int x, int y) {

    public Position translate(Direction direction) {
        return new Position(x + direction.dx(), y + direction.dy());
    }

    public int distanceSquared(Position other) {
        int dx = x - other.x;
        int dy = y - other.y;
        return dx * dx + dy * dy;
    }
}
