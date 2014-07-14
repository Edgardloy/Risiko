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



package ui.GUI.country;

import configuration.FightConfiguration;
import exceptions.*;
import interfaces.IFight;
import interfaces.data.IPlayer;
import server.logic.ClientEventProcessor;
import server.logic.IFightActionListener;
import ui.CUI.FightCUI;
import ui.GUI.utils.JExceptionDialog;
import ui.GUI.utils.JModalDialog;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;


public class JFightGUI extends JModalDialog {
    private final IFight fight;
    private final JFightSide aggressorSide;
    private final JFightSide defenderSide;
    private final ClientEventProcessor remoteEventsProcessor;
    private final ActionListener fightUpdateUIListener;
	private final IPlayer clientPlayer;

    private class UpdateUIFightListener  implements ActionListener{


        /**
         * Invoked when an action occurs.
         *
         * @param event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            try{
                update();

                if(JFightGUI.this.fight.getDefendersDice().size()>0){
                    JFightGUI.this.showResult();
                }

            }catch (RemoteException e){
                new JExceptionDialog(JFightGUI.this,e);
            }
        }

    }
    private class CloseBtnListener extends WindowAdapter implements ActionListener {


        /**
         * Prüft ob der Kampf geschlossen werden kann
         * @return
         */
        private boolean isValidToClose(){
            boolean isValid;
            try{
                isValid = JFightGUI.this.fight.isValidToClose();
            }catch (RemoteException e){
                new JExceptionDialog(JFightGUI.this,e);
                return false;
            }
            return isValid;
        }

        /**
         * Zeigt eine Nachricht an wenn
         */
        private void showNotValidToCloseMessage(){
            JModalDialog.showInfoDialog(JFightGUI.this,"Info", "Der Fight ist noch nicht abgeschlossen");
        }

        /**
         * Invoked when an action occurs.
         *
         * @param event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if(this.isValidToClose()) {
                JFightGUI.this.remoteEventsProcessor.removeUpdateUIListener(JFightGUI.this.fightUpdateUIListener);
                JFightGUI.this.dispose();
            }else {
                this.showNotValidToCloseMessage();
            }
        }
        /**
         * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
         *
         * @param e
         */
        @Override
        public void windowClosing(WindowEvent e) {

            if(this.isValidToClose()){
                JFightGUI.this.remoteEventsProcessor.removeUpdateUIListener(JFightGUI.this.fightUpdateUIListener);
                super.windowClosing(e);
            }
            else {
                this.showNotValidToCloseMessage();
            }
        }
    }
    public void showResult(){
        int[] result;
        try {
            result = JFightGUI.this.fight.getResult();
        }catch (RemoteException e){
            new JExceptionDialog(JFightGUI.this,e);
            return;
        }
        int defenderLostArmies = result[1];
        int aggressorLostArmies = result[0];
        int aggresorWon = result[2];
        if (aggresorWon == 1){
            JModalDialog.showInfoDialog(JFightGUI.this, "Angriff erfolgreich", "Der Angreifer hat das Land übernommen");


            /**
             * Block für Abfragen nach Armeen zum nachrücken
             */
            try{
                //Nur machen wenn aktueller Client auch gewinner ist
                if(this.clientPlayer.getId().equals(this.fight.getAggressor().getId())) {
                    /**
                     * Max anzahl ermitteln
                     */
                    int numberOfArmiesOnFromCountry;
                    try {
                        numberOfArmiesOnFromCountry = fight.getFrom().getArmySize();
                    } catch (RemoteException e) {
                        new JExceptionDialog(JFightGUI.this, e);
                        return;
                    }


                    int numberOfArmiesToMove = FightConfiguration.NUMBER_OF_ARMIES_TO_OCCUPIED_COUNTRY;

                    /**
                     * Anzahl der Armeen erfragen, die rübergeschoben werden sollen
                     */
                    try {
                        numberOfArmiesToMove = JModalDialog.showAskIntegerModal(JFightGUI.this, "Armeen nachziehen", "Bitte geben Sie an wieviele Armeen Sie nachziehen möchten", FightConfiguration.NUMBER_OF_ARMIES_TO_OCCUPIED_COUNTRY, numberOfArmiesOnFromCountry);
                    } catch (UserCanceledException e) {
                        JModalDialog.showInfoDialog(JFightGUI.this, "Benutzer Abbruch", "Sie haben keine Anzahl eingegbene es wurde nun die mindestanzahl von " + FightConfiguration.NUMBER_OF_ARMIES_TO_OCCUPIED_COUNTRY + " Armeen in ihr neues Land bewegt");
                    }


                    /**
                     * Anzahl versuchen zu verschieben
                     */
                    try {
                        JFightGUI.this.fight.moveArmiesAfterTakeover(numberOfArmiesToMove);
                    } catch (FightMoveMinimumOneArmy | ArmyAlreadyMovedException| FightNotWonException | RemoteException | NotTheOwnerException | RemoteCountryNotFoundException | ToManyNewArmysException | TurnNotAllowedStepException | TurnNotInCorrectStepException | NotEnoughArmysToMoveException | CountriesNotConnectedException  e) {
                        new JExceptionDialog(JFightGUI.this, e);
                    }
                }
            }catch (RemoteException e){
                new JExceptionDialog(JFightGUI.this,e);
            }

            //Close Window
            JFightGUI.this.dispose();

        }else{
            String str = String.format("Der Angreifer hat " + aggressorLostArmies + " Armeen verloren %n");
            str  += String.format("Der Verteidiger hat " + defenderLostArmies + " Armeen verloren %n");
            str  += "Der Kampf geht weiter ";
            JModalDialog.showInfoDialog(JFightGUI.this, "Erfolgreich verteidigt", str);
        }

    }
    public JFightGUI(final Component parent, final IFight fight, final ClientEventProcessor remoteEventsProcessor, IPlayer clientPlayer) throws RemoteException {
        super(parent,"Fight",ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new CloseBtnListener());
        this.fight = fight;
        this.clientPlayer = clientPlayer;
        this.setLayout(new BorderLayout(5,5));
        this.aggressorSide  = new JFightSide(this.fight, JFightSide.sides.AGGRESSOR, this.clientPlayer);
        this.defenderSide  = new JFightSide(this.fight, JFightSide.sides.DEFENDER, this.clientPlayer);
        this.remoteEventsProcessor = remoteEventsProcessor;
        this.fightUpdateUIListener = new UpdateUIFightListener();
        this.remoteEventsProcessor.addUpdateUIListener(fightUpdateUIListener);

        JPanel centerPanel = new JPanel();

        centerPanel.add(this.aggressorSide);
        centerPanel.add(this.defenderSide);

        centerPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(centerPanel,BorderLayout.CENTER);

        //Close btn
        JButton closeBtn = new JButton("Kampf verlassen");
        closeBtn.addActionListener(new CloseBtnListener());
        this.add(closeBtn,BorderLayout.SOUTH);

    }
    public void update() throws RemoteException{
        this.aggressorSide.update();
        this.defenderSide.update();
    }

    /**
     * Override the dispose method
     */
    @Override
    public void dispose(){
        this.remoteEventsProcessor.removeUpdateUIListener(this.fightUpdateUIListener);
        super.dispose();
    }

}
