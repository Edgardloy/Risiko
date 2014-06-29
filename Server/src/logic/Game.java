package logic;

import java.util.*;
import java.awt.Color;

import interfaces.IGame;
import interfaces.IRound;
import interfaces.data.IArmy;
import interfaces.data.ICountry;
import interfaces.data.IMap;
import interfaces.data.IPlayer;
import interfaces.data.cards.ICardDeck;
import logic.data.*;
import logic.data.Map;
import exceptions.*;
import logic.data.cards.CardDeck;
import logic.data.orders.OrderManager;
import persistence.dataendpoints.PersistenceEndpoint;

/**
 * @author Jennifer Theloy, Thu Nguyen, Stefan Bieliauskas
 *
 * Klasse für ein eizelnes Spiel. Diese dient zur Spielverwaltung.
 */
public class Game implements IGame {
	
	private Stack<Color> color = new Stack<Color>();
    /**
     * Legt die mindestanzahl an Spielern fest, die für ein Spiel erforderlich sind
     */
    public static final int minCountPlayers = 3;

    /**
     * Legt die maximalanzahl an Spielern fest, die für ein Spiel erforderlich sind
     */
    public static final int maxCountPlayers = 5;

    /**
     * Representiert die Karte des Spiels
     */
    private final IMap map;

    /**
     * Listet alle Spieler auf, die aktiv am Spiel teilnehmen.
     */
    private final List<IPlayer> players = new ArrayList<IPlayer>();

    /**
     * The current gamePanels state, default => WAITING
     */
    private IGame.gameStates currentGameState = IGame.gameStates.WAITING;

    /**
     * Contains the current round , default null
     */
    private  IRound currentRound;

    /**
     * Name für das SPiel, erstmal aktuelles Datum
     */
    private final String name = new Date().toString();;

    /**
     * Die UUID für das Spiel
     */
    private final UUID id;

    /**
     * Der GameManager, der für die Persistenz verwendet werden soll.
     */
    private final PersistenceEndpoint<IGame> persistenceEndpoint;
    /**
     *
     * @param persistenceEndpoint - Der Manager, der zur Speicherung des Spiels verwnedet werden soll
     */
    private ICardDeck deck;

    /**
     * Konstruktor, wenn das Spiel mit einer bestimmten Karte erstellt werden soll
     * @param persistenceEndpoint Endpunkt zum speichern des spiels
     * @param map Karte die für das Spiel verwnedet werden soll
     */
    public Game(PersistenceEndpoint<IGame> persistenceEndpoint) {
        this(persistenceEndpoint,new Map());
    }

    public Game(PersistenceEndpoint<IGame> persistenceEndpoint, IMap map){
    	this.map= map;
        this.persistenceEndpoint = persistenceEndpoint;
        this.id = UUID.randomUUID();
        this.color.add(Color.BLUE);
        this.color.add(Color.GREEN);
        this.color.add(Color.ORANGE);
        this.color.add(Color.RED);
        this.color.add(Color.MAGENTA);
        this.deck = new CardDeck(this.map.getCountries());
    }

    public ICardDeck getDeck(){
    	return this.deck;
    }

    /**
     * Startet das Spiel
     * @throws NotEnoughPlayerException
     * @throws TooManyPlayerException
     * @throws NotEnoughCountriesException
     * @throws GameAllreadyStartedException
     * @throws PlayerAlreadyHasAnOrderException
     */
    public void onGameStart() throws NotEnoughPlayerException, TooManyPlayerException, NotEnoughCountriesException, GameAllreadyStartedException, PlayerAlreadyHasAnOrderException {

        //Exception-Handling
        if (this.players.size() < Game.minCountPlayers) {
            throw new NotEnoughPlayerException(Game.minCountPlayers);
        } else if (this.players.size() > Game.maxCountPlayers) {
            throw new TooManyPlayerException(Game.maxCountPlayers);
        } else if (this.map.getCountries().size() < this.players.size()) {
            throw new NotEnoughCountriesException(this.map.getCountries().size());
        } else if (this.currentGameState != IGame.gameStates.WAITING) {
            throw new GameAllreadyStartedException();
        }

        //Spielstart
        this.distributeCountries();
        this.setDefaultArmys();
        OrderManager.createOrdersForPlayers(this.getPlayers(),this);

        this.currentGameState = IGame.gameStates.RUNNING;
        this.setCurrentRound(new Round(this.deck, players, map, Turn.getDefaultStepsFirstRound()));


    }


    /**
     * Versetzt das Spiel in die nächste Runde
     * @throws ToManyNewArmysException
     * @throws RoundNotCompleteException
     * @throws GameNotStartedException
     * @throws GameIsCompletedException
     */
    public void setNextRound() throws ToManyNewArmysException, RoundNotCompleteException,GameNotStartedException, GameIsCompletedException{
        if (this.currentRound != null) {
            if (!this.currentRound.isComplete()) {
                throw new RoundNotCompleteException();
            }
        }
        if (this.getCurrentGameState() == IGame.gameStates.WAITING){
            throw  new GameNotStartedException();
        }
        else if(this.isGameWon()){
            throw  new GameIsCompletedException();
        }
        this.currentRound = new Round(this.deck, this.players, this.map);
    }

    /**
     * Gibt die Aktelle Runde des Spielers zurück
     * @return
     * @throws GameNotStartedException
     */
    public IRound getCurrentRound() throws GameNotStartedException {
        if (this.getCurrentGameState() == IGame.gameStates.WAITING) {
            throw new GameNotStartedException();
        }
        return this.currentRound;
    }

    /**
     * Gibt den aktuellen Status des Spiels zurück
     * @return Status des Spiels
     */
    public IGame.gameStates getCurrentGameState() {
        return this.currentGameState;
    }


    /**
     * Verteilt die Länder beim Spielstart an alle angemeldeten Spieler.
     *
     */
    private void distributeCountries() {
        /**
         * Stack, der die Länder beinhaltet, die noch zu verteilen sind
         */
        Stack<ICountry> countriesStack = new Stack<ICountry>();
        countriesStack.addAll(this.map.getCountries());
        Collections.shuffle(countriesStack); // Durchmischen der Länder

        /**
         * Durchläuft die Schleife so lange, bis die Anzahl der L�nder, die noch zu verteilen sind,
         * kleiner ist, als die Anzahl der Spieler
         */
        while (!countriesStack.empty()) {

            for (IPlayer p : players) {
                if(!countriesStack.empty()){
                    p.addCountry(countriesStack.pop());
                }
                else {
                    break;
                }
            }
        }
    }

    /**
     *
     * Wird beim Spielstart aufgerufen und setzt für alle Länder genau 1 Armee
     *
     *
     */
    private void setDefaultArmys() {
        for (IPlayer player : players) {
            for (ICountry country : player.getCountries()) {
                //Nur machen, wenn noch keine Armee auf dem Land sitzt
                if (country.getArmyList().size() == 0) {
                    IArmy a = new Army(player);
                    try {
                        country.addArmy(a);
                    } catch (CountriesNotConnectedException e) {
                        //Kann nicht auftreten, da die diefalut-Armys zuerst keinem Land zugewiesen wurden.
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * Wird ausgelöst, wenn durch die GUI ein Spieler das Spiel verlässt
     *
     * @param player - Player der gelöscht werden soll
     *
     * @throws exceptions.PlayerNotExsistInGameException
     */
    public void onPlayerDelete(final IPlayer player) throws PlayerNotExsistInGameException {
        try {
            this.players.remove(player);
        } catch (final Exception e) {
            throw new PlayerNotExsistInGameException(player);
        }
    }


    /**
     * Wird ausgelöst, sobald über die GUI ein neuer Spieler hinzugefügt wird.
     *
     * @param name - Der Name des neuen Spielers
     */
    public void onPlayerAdd(final String name) throws GameAllreadyStartedException {

        if (this.getCurrentGameState() != IGame.gameStates.WAITING) {
            throw new GameAllreadyStartedException();
        } else {
            IPlayer newPlayer = new Player(name, this.color.pop());
            this.addPlayer(newPlayer);
        }
    }

    /**
     * Pürft, ob das Spiel gewonnen wurde
     * @return Wenn gewonnen true
     */
    private boolean isGameWon() throws GameNotStartedException{
        if(this.getCurrentGameState() == IGame.gameStates.WAITING){
            throw new GameNotStartedException();
        }
        if(this.getWinner()!=null){
            return true;
        }
        return false;
    }


    /**
     * Gibt den Gewinner zurück, der das Spiel gewonnen hat
     * Wenn keiner gewonnen hat gibt die Methode null zurück
     * @return Sieger des Spiels
     */
    public IPlayer getWinner (){
        for(IPlayer player : players){
            if(player.getOrder().isCompleted()){
                return player;
            }
        }
        return null;
    }

    /**
     * Setzt die aktuelle Runde
     *
     * @param r
     */
    public void setCurrentRound(IRound r) {
        this.currentRound = r;
    }

    /**
     * Setzt den aktuellen gameState
     *
     * @param s
     */
    public void setCurrentGameState(IGame.gameStates s) {
        this.currentGameState = s;
    }

    /**
     * Für alle Spieler der Spielerliste hinzu
     *
     * @param players
     */
    public void addPlayers(List<IPlayer> players) {
        this.players.addAll(players);
    }

    /**
     * Gibt die Karte des Spiels zurück
     *
     * @return
     */
    public IMap getMap() {
        return map;
    }

    /**
     * Für dem Spiel einen neuen Spieler hinzu
     *
     * @param player - neuer Spieler
     */
    public void addPlayer(final IPlayer player) {
        if (player.getColor() == null){
            player.setColor(this.color.pop());
        }
        this.players.add(player);
    }

    /**
     * @return Liste der Spieler
     */
    public List<IPlayer> getPlayers() {
        return this.players;
    }

    /**
     * Getter für die ID
     * @return UUID des Spiels
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gibt das Spiel als String aus.
     * @return Gibt "IGame" zurück
     */
    @Override
    public String toString() {
        return "Game" + this.name;
    }

    /**
     * Speicher das Spiel ab
     *
     */
    public boolean save () throws PersistenceEndpointIOException{
        return this.persistenceEndpoint.save(this);
    }
}
