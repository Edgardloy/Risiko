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

package Client.ui.GUI.country;
import commons.interfaces.IFight;
import commons.interfaces.ITurn;
import commons.interfaces.data.ICountry;
import commons.interfaces.data.IPlayer;
import Client.logic.ClientEventProcessor;
import Client.ui.GUI.utils.JExceptionDialog;
import Client.ui.GUI.utils.JModalDialog;
import commons.exceptions.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

/**
 * Klasse dient zur Auswahl eines Landes das man Angreifen möchte
 *
 */
public class JCountryFightMenu extends JCountryNeighborsMenu {

    private final ITurn turn;
    private final ICountry country;
	private final IPlayer clientPlayer;
    private final ClientEventProcessor remoteEventProcessor;
    public class NeighborActionListener implements ActionListener{

        /**
         * Invoked when an action occurs.
         *
         * @param event
         */
        @Override
        public void actionPerformed(ActionEvent event){
            if(event.getActionCommand().equals("onCountryClick")){
                ICountry from    =  JCountryFightMenu.this.country;
                ICountry to      =  JCountryFightMenu.this.getSelectedNeighborsMenuItem().getCountry();
                IFight fight;
                try {
                    fight = JCountryFightMenu.this.turn.fight(from, to, clientPlayer);
                }catch (TurnNotInCorrectStepException | NotYourTurnException | TurnNotAllowedStepException | ToManyNewArmysException | NotTheOwnerException | RemoteException | RemoteCountryNotFoundException e ){
                    new JExceptionDialog(JCountryFightMenu.this,e);
                    return;
                }
                JPopupMenu menu = (JPopupMenu) JCountryFightMenu.this.getParent();
                try {
                    JModalDialog modal = new JFightGUI(menu.getInvoker(),fight,JCountryFightMenu.this.remoteEventProcessor, clientPlayer);
                    SwingUtilities.invokeLater(modal);
                }catch (RemoteException e){
                    new JExceptionDialog(JCountryFightMenu.this,e);
                    return;
                }

            }
        }
    }

    /**
     * Klasse dient zur Auswahl des Landes das man angreifen möchte
     * @param country Land von dem Aus der Angrif ausgeht
     * @param turn Server-Objekt
     * @param remoteEventProcessor RemoteManager der die Server events verwaltet
     * @param clientPlayer Spieler der die Aktion ausführen mlchte
     * @throws RemoteException
     */
    public JCountryFightMenu(final ICountry country, final ITurn turn, final ClientEventProcessor remoteEventProcessor, IPlayer clientPlayer) throws RemoteException{
        super("Fight",country);
        this.country = country;
        this.clientPlayer = clientPlayer;
        this.turn = turn;
        this.remoteEventProcessor = remoteEventProcessor;
        this.addActionListener(new NeighborActionListener());

    }
}
