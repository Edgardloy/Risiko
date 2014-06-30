package logic.data.orders;

import interfaces.data.IContinent;
import interfaces.data.IPlayer;
import interfaces.data.Orders.IOrder;
import logic.data.Continent;
import logic.data.Player;


public class OrderTakeOverContinents extends AbstractOrder implements IOrder {
	/**
	 * Erster Kontinent, der erobert werden soll
	 */
	private IContinent continentOne;
	/**
	 * zweiter Kontinent, der erobert werden soll
	 */
	private IContinent continentTwo;

	/**
	 * der zweite Constructor ist für die Erstellung einer Order, bei der nur zwei bestimmte Kontinente übernommen werden müssen
	 * Die Kontinentliste wird in diesem Fall auf null gesetzt, da es keinen dritten Kontinent zu ermitteln gibt
	 * @param continent1 erster zu übernehmender Kontinent
	 * @param continent2 zweiter zu übernehmender Kontinent
	 * @param agend Spieler, dem die Order zugewiesen ist
	 */
	public OrderTakeOverContinents(IContinent continent1, IContinent continent2, IPlayer agend) {
        super(agend);
		this.continentOne = continent1;
		this.continentTwo = continent2;

	}
	
	/**
	 * Überprüft, ob die Order erfüllt wurde (Für beide Fälle)
	 */
	@Override
	public boolean isCompleted() {
		if(this.agent == this.continentOne.getCurrentOwner() && this.agent == this.continentTwo.getCurrentOwner()){
			return true;
		}
		return false;
	}


    @Override
    public String toString(){
        return this.agent + " hat die Aufgabe " + continentOne + " und " + continentTwo + " zu erobern. ";
    }
}
