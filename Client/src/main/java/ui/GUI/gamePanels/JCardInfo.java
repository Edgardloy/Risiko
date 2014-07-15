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



package ui.GUI.gamePanels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import ui.GUI.utils.JExceptionDialog;
import exceptions.ExchangeNotPossibleException;
import exceptions.GameNotStartedException;
import exceptions.NotEnoughCardsToExchangeException;
import exceptions.ToManyNewArmysException;
import exceptions.TurnNotAllowedStepException;
import exceptions.TurnNotInCorrectStepException;
import interfaces.IGame;
import interfaces.data.IPlayer;
import interfaces.data.cards.ICard;
import interfaces.data.cards.ICardDeck;

public class JCardInfo extends JFrame{
	/**
	 * Aktuelles Spiel
	 */
	private final IGame game;
	/**
	 * Der Spieler de jeweiligen Clients
	 */
	private final IPlayer player;
	/**
	 * Der Infotext zu den Karten
	 */
	private final JTextArea cardInfo = new JTextArea("");
	/**
	 *
	 */
	private JPanel context;
	/**
	 * Der Button zum eintauschen der Karten
	 */
	private JButton exchange;
	/**
	 * Das Kartendeck
	 */
	private ICardDeck deck;
	
	private class ExchangeActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				game.getCurrentRound().getCurrentTurn().exchangeCards(player);
				update();
			} catch (RemoteException | NotEnoughCardsToExchangeException | ToManyNewArmysException | ExchangeNotPossibleException | TurnNotAllowedStepException | TurnNotInCorrectStepException | GameNotStartedException e) {
				new JExceptionDialog(e);
				return;
			}
		}
		
	}
	/**
	 * Konstruktor
	 * @param player Der spielen des jeweilligen Clients
	 * @param deck das Karten Deck
	 * @throws RemoteException
	 */
	public JCardInfo(IPlayer player, IGame game) throws RemoteException{
        super();
        this.game = game;
		this.player = player;
		this.deck = this.game.getDeck();
		this.context = new JPanel();
		this.context.setLayout(new GridLayout(2,1));
		this.cardInfo.setWrapStyleWord(true);
		this.cardInfo.setLineWrap(true);
		this.exchange = new JButton("eintauschen");
		this.exchange.addActionListener(new ExchangeActionListener());
		setContext();
	}
	/**
	 * Setzt den Kontext des Panels
	 * @throws RemoteException
	 */
	private void setContext() throws RemoteException{
		update();
		this.context.add(this.cardInfo);
		this.context.add(this.exchange);
	}
	/**
	 * Zum Updaten des Panels
	 * @throws RemoteException
	 */
	public void update() throws RemoteException{
		List<? extends ICard> cards = this.player.getCards();

        String msg = "";
        Iterator<? extends ICard> iter = cards.iterator();
        while (iter.hasNext()){
            ICard currentCard = iter.next();
            msg += String.format(currentCard.toStringRemote() + "%n");
        }

		this.cardInfo.setText(msg);
	}
	/**
	 * 
	 * @return context
	 */
	public JPanel getContext(){
		return this.context;
	}
}
