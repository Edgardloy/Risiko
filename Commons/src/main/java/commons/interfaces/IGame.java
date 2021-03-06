/*
 * RISIKO-JAVA - Game, Copyright 2014  Jennifer Theloy, Stefan Bieliauskas  -  All Rights Reserved.
 * Hochschule Bremen - University of Applied Sciences
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * Contact:
 *     Jennifer Theloy: jTheloy@stud.hs-bremen.de
 *     Stefan Bieliauskas: sBieliauskas@stud.hs-bremen.de
 *
 * Web:
 *     https://github.com/B-Stefan/Risiko
 *
 */

package commons.interfaces;

import commons.exceptions.*;
import commons.interfaces.data.IMap;
import commons.interfaces.data.IPlayer;
import commons.interfaces.data.cards.ICardDeck;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Stefan on 29.06.14.
 */
public interface IGame extends Remote, Serializable, IToStringRemote {

    public static enum gameStates {
        WAITING, // Wait for start input
        RUNNING, // The gamePanels passes through the rounds
        FINISHED // One player finished his order
    }
    /**
     * Startet das Spiel
     * @throws commons.exceptions.NotEnoughPlayerException
     * @throws commons.exceptions.TooManyPlayerException
     * @throws commons.exceptions.NotEnoughCountriesException
     * @throws commons.exceptions.GameAlreadyStartedException
     * @throws commons.exceptions.PlayerAlreadyHasAnOrderException
     */
    public void onGameStart() throws RemoteException, NotEnoughPlayerException, TooManyPlayerException, NotEnoughCountriesException, GameAlreadyStartedException, PlayerAlreadyHasAnOrderException;



    /**
     * Versetzt das Spiel in die nächste Runde
     * @throws ToManyNewArmysException
     * @throws RoundNotCompleteException
     * @throws GameNotStartedException
     * @throws GameIsCompletedException
     */
    public void setNextRound() throws ToManyNewArmysException, RoundNotCompleteException,GameNotStartedException, GameIsCompletedException,RemoteException;

    /**
     * Gibt die Aktelle Runde des Spielers zurück
     * @return
     * @throws GameNotStartedException
     */
    public IRound getCurrentRound() throws GameNotStartedException,RemoteException;

    /**
     * Gibt den aktuellen Status des Spiels zurück
     * @return Status des Spiels
     */
    public gameStates getCurrentGameState() throws RemoteException;

    /**
     * Wird ausgelöst, wenn durch die GUI ein Spieler das Spiel verlässt
     *
     * @param player - Player der gelöscht werden soll
     *
     * @throws commons.exceptions.PlayerNotExistInGameException
     */
    public void onPlayerDelete(final IPlayer player) throws PlayerNotExistInGameException,RemoteException;


    /**
     * Gibt den Gewinner zurück, der das Spiel gewonnen hat
     * Wenn keiner gewonnen hat gibt die Methode null zurück
     * @return Sieger des Spiels
     */
    public IPlayer getWinner () throws RemoteException;

    /**
     * Gibt die Karte des Spiels zurück
     *
     * @return
     */
    public IMap getMap() throws RemoteException;


    /**
     * FÜgt dem Spiel den Spieler mit dem Namem hinzu
     * @param name Name des Neuen Spielers
     * @param client client object des Spielers für Brodcast
     * @return Den neuen Spieler
     * @throws PlayerNameAlreadyChooseException
     * @throws RemoteException
     */
    public IPlayer addPlayer(final String name, final IClient client) throws GameAlreadyStartedException,PlayerNameAlreadyChooseException,RemoteException;

    /**
     * Gibt den Spieler zum angegebenen Namen zurück
     * @param name - Name des gesuchten Spielers
     * @return Spieler
     * @throws commons.exceptions.PlayerNotExistInGameException Wenn Spieler nicht gefunden wird
     * @throws RemoteException
     */
    public IPlayer getPlayer(final String name) throws PlayerNotExistInGameException, RemoteException;

    /**
     * @return Liste der Spieler
     */
    public List<? extends IPlayer> getPlayers() throws RemoteException;


    /**
     * Getter für die ID
     * @return UUID des Spiels
     */
    public UUID getId() throws RemoteException;

    /**
     * Speicher das Spiel ab
     *
     */
    public boolean save () throws PersistenceEndpointIOException, RemoteException;


    /**
     * Gibt das CardDeck zurück
     * @return
     * @throws RemoteException
     */
    public ICardDeck getDeck() throws RemoteException;

    /**
     * Setzt für einen Spieler den client
     * @param player Speiler für den der client gesetzt werden soll
     * @param client client der gesetzt werden soll
     */
    public void setClient(IPlayer player, IClient client) throws RemoteException, PlayerNotExistInGameException;

    }
