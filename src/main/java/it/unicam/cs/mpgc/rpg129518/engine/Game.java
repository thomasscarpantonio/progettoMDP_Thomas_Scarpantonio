package it.unicam.cs.mpgc.rpg129518.engine;

import it.unicam.cs.mpgc.rpg129518.content.DungeonDefinition;
import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.model.combat.Combat;
import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import it.unicam.cs.mpgc.rpg129518.model.item.Consumable;
import it.unicam.cs.mpgc.rpg129518.model.item.Equippable;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import it.unicam.cs.mpgc.rpg129518.model.item.ItemStack;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129518.model.world.Direction;
import it.unicam.cs.mpgc.rpg129518.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg129518.model.world.DungeonGenerator;
import it.unicam.cs.mpgc.rpg129518.model.world.DungeonRun;
import it.unicam.cs.mpgc.rpg129518.model.world.GameMap;
import it.unicam.cs.mpgc.rpg129518.model.world.MapObject;
import it.unicam.cs.mpgc.rpg129518.model.world.Position;
import it.unicam.cs.mpgc.rpg129518.model.world.Room;
import it.unicam.cs.mpgc.rpg129518.model.world.RoomOutcome;
import it.unicam.cs.mpgc.rpg129518.persistence.PersistenceException;
import it.unicam.cs.mpgc.rpg129518.persistence.SaveData;
import it.unicam.cs.mpgc.rpg129518.persistence.SaveMapper;
import it.unicam.cs.mpgc.rpg129518.persistence.SaveRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Facade della logica di gioco: unico punto di ingresso per qualsiasi interfaccia utente.
 * Orchestra le classi del modello, applica la macchina a stati ({@link GamePhase}) e
 * notifica gli osservatori. Non contiene alcun codice grafico.
 *
 * Il giocatore si muove su una {@link GameMap}: quella del villaggio (fase HUB) o quella di un dungeon
 * (fase EXPLORING). Urtando un oggetto bloccante si interagisce con esso (porta, mostro...).
 */
public class Game {

    public static final int INN_PRICE = 10;
    /** Raggio (in caselle) entro cui il giocatore vede nei dungeon. */
    public static final int VIEW_RADIUS = 5;
    private static final int SMALL_POTIONS_AT_START = 2;

    private final GameContent content;
    private final SaveRepository repository;
    private final Random random;
    private final DungeonGenerator generator;
    private final List<GameListener> listeners = new ArrayList<>();
    private final Set<String> completedDungeons = new LinkedHashSet<>();

    private GamePhase phase = GamePhase.NOT_STARTED;
    private Player player;
    private GameMap map;
    private Position position;
    private Direction facing = Direction.DOWN;
    private DungeonRun run;
    private Combat combat;
    private MapObject engagedObject;

    public Game(GameContent content, SaveRepository repository, Random random) {
        this.content = content;
        this.repository = repository;
        this.random = random;
        this.generator = new DungeonGenerator(content, random);
    }

    // ================= osservatori =================

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    private void emit(String message) {
        listeners.forEach(l -> l.onMessage(message));
    }

    private void notifyStateChanged() {
        listeners.forEach(GameListener::onStateChanged);
    }

    // ================= partita =================

    public void newGame(String playerName, PlayerClass playerClass) {
        player = new Player(playerName, playerClass);
        if (content.item(playerClass.getStartingWeaponId()) instanceof Weapon weapon) {
            player.equip(weapon);
        }
        player.getInventory().add(content.item("small_potion"), SMALL_POTIONS_AT_START);
        completedDungeons.clear();
        enterVillage();
        emit("Benvenuto, " + playerName + " il " + playerClass.getDisplayName() + "! La tua avventura ha inizio.");
        emit("Muoviti con WASD o le frecce. Urta le porte e i cancelli per entrare.");
        notifyStateChanged();
    }

    public boolean hasSave(PlayerClass playerClass) {
        try {
            return repository.exists(playerClass.name());
        } catch (PersistenceException e) {
            return false;
        }
    }

    /** @return true se il salvataggio è stato caricato con successo. */
    public boolean loadGame(PlayerClass playerClass) {
        try {
            Optional<SaveData> data = repository.load(playerClass.name());
            if (data.isEmpty()) {
                return false;
            }
            player = SaveMapper.toPlayer(data.get(), content);
            completedDungeons.clear();
            completedDungeons.addAll(data.get().completedDungeons());
            enterVillage();
            emit("Partita caricata. Bentornato, " + player.getName() + "!");
            notifyStateChanged();
            return true;
        } catch (PersistenceException | IllegalArgumentException e) {
            return false;
        }
    }

    public void saveGame() {
        if (phase != GamePhase.HUB) {
            emit("Puoi salvare solo al villaggio.");
            return;
        }
        try {
            repository.save(player.getPlayerClass().name(), SaveMapper.toSaveData(player, completedDungeons));
            emit("Partita salvata.");
        } catch (PersistenceException e) {
            emit("Errore di salvataggio: " + e.getMessage());
        }
    }

    private void enterVillage() {
        run = null;
        combat = null;
        engagedObject = null;
        map = content.createVillage();
        position = map.getStart();
        facing = Direction.DOWN;
        phase = GamePhase.HUB;
    }

    // ================= movimento =================

    /** Muove il giocatore di una casella; se la casella contiene un oggetto bloccante, interagisce con esso. */
    public void move(Direction direction) {
        if (phase != GamePhase.HUB && phase != GamePhase.EXPLORING) {
            return;
        }
        boolean turned = facing != direction;
        facing = direction;
        Position target = position.translate(direction);
        if (!map.isWalkable(target)) {
            if (turned) {
                notifyStateChanged();
            }
            return;
        }
        Optional<MapObject> object = map.objectAt(target).filter(MapObject::isActive);
        if (object.isEmpty()) {
            stepTo(target);
        } else if (phase == GamePhase.HUB) {
            interactWithVillage(object.get());
        } else {
            interactWithDungeonObject(object.get(), target);
        }
        notifyStateChanged();
    }

    private void stepTo(Position target) {
        position = target;
        if (phase == GamePhase.EXPLORING) {
            map.reveal(position, VIEW_RADIUS);
        }
    }

    private void interactWithVillage(MapObject object) {
        switch (object.getType()) {
            case INN -> restAtInn();
            case SHOP -> listeners.forEach(GameListener::onShopRequested);
            case SAVE_POINT -> saveGame();
            case GATE -> enterDungeon(object.getRefId());
            default -> { }
        }
    }

    private void interactWithDungeonObject(MapObject object, Position target) {
        Room room = object.getEncounter().orElseThrow();
        boolean blocking = object.isBlocking();
        RoomOutcome outcome = room.enter(player, random);
        outcome.messages().forEach(this::emit);
        if (!blocking) {
            stepTo(target);
            object.deactivate();
        }
        if (!player.isAlive()) {
            gameOver();
        } else {
            outcome.encounter().ifPresent(monster -> startCombat(monster, object));
        }
    }

    // ================= villaggio =================

    public List<DungeonDefinition> getDungeons() {
        return content.getDungeons();
    }

    /** Il primo dungeon è sempre aperto; gli altri si sbloccano completando il precedente. */
    public boolean isUnlocked(DungeonDefinition definition) {
        int index = content.getDungeons().indexOf(definition);
        return index == 0 || completedDungeons.contains(content.getDungeons().get(index - 1).id());
    }

    public boolean isGateUnlocked(String dungeonId) {
        return findDungeon(dungeonId).map(this::isUnlocked).orElse(false);
    }

    public boolean isCompleted(DungeonDefinition definition) {
        return completedDungeons.contains(definition.id());
    }

    public boolean allDungeonsCompleted() {
        return content.getDungeons().stream().allMatch(this::isCompleted);
    }

    private Optional<DungeonDefinition> findDungeon(String id) {
        return content.getDungeons().stream().filter(d -> d.id().equals(id)).findFirst();
    }

    public void enterDungeon(String dungeonId) {
        if (phase != GamePhase.HUB) {
            return;
        }
        Optional<DungeonDefinition> definition = findDungeon(dungeonId);
        if (definition.isEmpty()) {
            return;
        }
        if (!isUnlocked(definition.get())) {
            emit("Il cancello è sbarrato: devi prima completare il dungeon precedente.");
            return;
        }
        Dungeon dungeon = generator.generate(definition.get());
        run = new DungeonRun(dungeon);
        map = run.getMap();
        position = map.getStart();
        map.reveal(position, VIEW_RADIUS);
        phase = GamePhase.EXPLORING;
        emit("Entri in: " + dungeon.getName() + ". Sconfiggi il boss per completarlo!");
    }

    public void restAtInn() {
        if (phase != GamePhase.HUB) {
            return;
        }
        if (!player.spendGold(INN_PRICE)) {
            emit("Non hai abbastanza oro per la locanda (" + INN_PRICE + ").");
        } else {
            player.restoreFully();
            emit("Dormi alla locanda e ti svegli completamente rinvigorito.");
        }
        notifyStateChanged();
    }

    public List<Item> getShopItems() {
        return content.getShopItems();
    }

    public void buy(String itemId) {
        if (phase != GamePhase.HUB) {
            return;
        }
        Item item = content.item(itemId);
        if (player.spendGold(item.getValue())) {
            player.getInventory().add(item);
            emit("Acquisti " + item.getName() + " per " + item.getValue() + " oro.");
        } else {
            emit("Non hai abbastanza oro per " + item.getName() + ".");
        }
        notifyStateChanged();
    }

    /** Vende un oggetto dello zaino a metà del suo valore. */
    public void sell(String itemId) {
        if (phase != GamePhase.HUB) {
            return;
        }
        player.getInventory().findById(itemId).ifPresent(item -> {
            player.getInventory().remove(item);
            int price = item.getValue() / 2;
            player.addGold(price);
            emit("Vendi " + item.getName() + " per " + price + " oro.");
        });
        notifyStateChanged();
    }

    public void abandonDungeon() {
        if (phase != GamePhase.EXPLORING) {
            return;
        }
        emit("Abbandoni il dungeon e fai ritorno al villaggio.");
        returnToHub();
        notifyStateChanged();
    }

    // ================= combattimento =================

    private void startCombat(Monster monster, MapObject source) {
        combat = new Combat(player, monster, random);
        engagedObject = source;
        phase = GamePhase.COMBAT;
    }

    public void attack() {
        combatAction(() -> combat.attack());
    }

    public void useAbility() {
        combatAction(() -> combat.useAbility());
    }

    public void defend() {
        combatAction(() -> combat.defend());
    }

    public void flee() {
        combatAction(() -> combat.flee());
    }

    private void combatAction(Supplier<List<String>> action) {
        if (phase != GamePhase.COMBAT) {
            return;
        }
        action.get().forEach(this::emit);
        switch (combat.getState()) {
            case ONGOING -> { }
            case VICTORY -> onCombatWon();
            case DEFEAT -> gameOver();
            case FLED -> {
                emit("Riesci a fuggire! Il nemico resta in agguato.");
                combat = null;
                engagedObject = null;
                phase = GamePhase.EXPLORING;
            }
        }
        notifyStateChanged();
    }

    private void onCombatWon() {
        boolean wasBoss = combat.getMonster().isBoss();
        engagedObject.deactivate();
        engagedObject = null;
        combat = null;
        if (wasBoss) {
            completeDungeon();
        } else {
            phase = GamePhase.EXPLORING;
        }
    }

    private void completeDungeon() {
        Dungeon dungeon = run.getDungeon();
        completedDungeons.add(dungeon.getId());
        Item reward = dungeon.getCompletionReward();
        player.getInventory().add(reward);
        emit("*** " + dungeon.getName() + " completato! Ricompensa: " + reward.getName() + " ***");
        if (allDungeonsCompleted()) {
            emit("Hai ripulito ogni dungeon: sei una leggenda! Puoi continuare a esplorare.");
        }
        returnToHub();
    }

    private void returnToHub() {
        enterVillage();
    }

    private void gameOver() {
        run = null;
        combat = null;
        engagedObject = null;
        phase = GamePhase.GAME_OVER;
        emit("Sei caduto in battaglia. GAME OVER. Puoi ricaricare l'ultimo salvataggio dal menu.");
    }

    // ================= oggetti =================

    /** Usa (pozioni) o equipaggia (armi/armature) un oggetto dello zaino. In combattimento consuma il turno. */
    public void useItem(String itemId) {
        if (player == null || phase == GamePhase.GAME_OVER) {
            return;
        }
        Optional<Item> found = player.getInventory().findById(itemId);
        if (found.isEmpty()) {
            return;
        }
        Item item = found.get();
        if (phase == GamePhase.COMBAT) {
            combatAction(() -> combat.useItem(item));
            return;
        }
        switch (item) {
            case Consumable consumable -> {
                player.getInventory().remove(item);
                emit(consumable.consume(player));
            }
            case Equippable equippable -> {
                player.getInventory().remove(item);
                emit(equippable.equipOn(player));
            }
            default -> emit(item.getName() + " non può essere usato.");
        }
        notifyStateChanged();
    }

    // ================= accesso in sola lettura =================

    public GamePhase getPhase() {
        return phase;
    }

    public Player getPlayer() {
        return player;
    }

    /** La mappa su cui il giocatore si sta muovendo (villaggio o dungeon). */
    public Optional<GameMap> getMap() {
        return Optional.ofNullable(map);
    }

    public Position getPosition() {
        return position;
    }

    public Direction getFacing() {
        return facing;
    }

    /** "village" oppure l'id del dungeon corrente: serve all'interfaccia per scegliere lo stile grafico. */
    public String getTheme() {
        return run == null ? "village" : run.getDungeon().getId();
    }

    public Optional<DungeonRun> getRun() {
        return Optional.ofNullable(run);
    }

    public Optional<Combat> getCombat() {
        return Optional.ofNullable(combat);
    }

    public List<ItemStack> getInventory() {
        return player == null ? List.of() : player.getInventory().getStacks();
    }
}