package interfaces;

import exceptions.NotYourTurnException;
import exceptions.RoundCompleteException;
import exceptions.ToManyNewArmysException;
import exceptions.TurnNotCompleteException;
import interfaces.data.IPlayer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IRound  extends Remote, Serializable, IToStringRemote {
    /**
     * Setzt den nächsten Spieler als aktuellen Spieler
     * @throws RoundCompleteException
     */
    public void setCurrentPlayer() throws RoundCompleteException,RemoteException;

    /**
     * Getter für den aktuellen Spieler
     * @return currentPayler: gibt aktuellen Spieler
     */
    public IPlayer getCurrentPlayer() throws RemoteException;



    /**
     * Erzeugt und setzt den nächsten Turn, wenn erlaubt
     * @throws exceptions.ToManyNewArmysException
     * @throws exceptions.TurnNotCompleteException
     * @throws RoundCompleteException
     */
    public void setNextTurn(IPlayer clientPlayer) throws ToManyNewArmysException, NotYourTurnException, TurnNotCompleteException, RoundCompleteException,RemoteException;


    /**
     * Pürft, ob die Runde komplett abgeschlossen ist, wenn ja True
     * @return True wenn Runde abgeschlossen ist
     * @throws ToManyNewArmysException
     */
    public boolean isComplete() throws ToManyNewArmysException,RemoteException;

    /**
     * GIbt den aktuellen Turn zurück
     * @return
     */
    public ITurn getCurrentTurn() throws RemoteException;

   }
