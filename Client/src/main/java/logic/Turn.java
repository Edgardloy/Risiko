package main.java.logic;
import main.java.logic.data.*;
import main.java.logic.exceptions.*;

import java.security.acl.NotOwnerException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Der Turn bildet einen einzelnen Zug eines Spielers ab.
 * Dabei druchläuft ein Turn verschiende Schritte (steps) Der Turn wird durch die Runde (Round) erstellt.
 *
 */
public class Turn {

    /**
     * Die Steps bilden die möglichen Schritte eines Turns ab
      */
    public  static enum steps {
        DISTRIBUTE,
        FIGHT,
        MOVE
    }

    /**
     * Gibt die Standardschritte zurück, die ein Turn normalerweise druchläuft
     * @return - Standard Schritte
     */
    public static Queue<steps> getDefaultSteps (){
        Queue<steps> s = new LinkedBlockingQueue<steps>(3) {
        };
        s.add(steps.DISTRIBUTE);
        s.add(steps.FIGHT);
        s.add(steps.MOVE);
        return s;
    }

    /**
     * Gibt die Schritte zurück, die alle Spieler in der ersten Runde druchlaufen müssen
     * @return - Schritte für die erste Runde
     */
    public static Queue<steps> getDefaultStepsFirstRound (){
        Queue<steps> s = new LinkedBlockingQueue<steps>(3) {
        };
        s.add(steps.DISTRIBUTE);
        s.add(steps.FIGHT);
        s.add(steps.MOVE);
        return s;
    }


    /**
     * Bildet den Spieler ab, dir diesen Turn durchführen musss
     */
    private final Player player;
    /**
     * Bildet die Karte ab auf dem der Spieler diesen Zug durchführt
     */
	private final main.java.logic.data.Map map;
    /**
     * Bildet ein Stack mit neuen Armeen ab, die der Spieler auf dem Spielfeld verteilen muss
     */
	private final Stack<Army> newArmies = new Stack<Army>();

    /**
     * Bildet die Liste der Armeen ab, die in diesem Zug bereits bewegt wurden
     */
	private final ArrayList<Army> movedArmies = new ArrayList<Army>();

    /**
     * Bildet die Warteschlange der Steps ab, die der Spieler noch durchlaufen muss.
     * Dabei befindet sich der current step nicht mehr in der Liste.
     * @see #currentStep
     * @see java.util.concurrent.LinkedBlockingQueue
     */
    public  final Queue<steps> allowedSteps;

    /**
     * Bildet die aktuelle Stufe des Zuges ab.
     * Diese Varriable kann nie den Wert null haben
     * @see #Turn(Player, main.java.logic.data.Map, java.util.Queue)
     */
    private steps currentStep;


    /**
     * Constructor für den Turn, der einen Turn inizialisiert
     * @param p - Player, der den Turn druchführen muss
     * @param m - Karte auf dem der Spieler sich bewegt
     * @param steps - Die geforderten Steps, die der Turn druchlaufen soll
     */
    public Turn(final Player p,final main.java.logic.data.Map m,final  Queue<steps> steps){
        this.player = p;
        this.map = m;

        //Argumentprüfung
        if(steps.isEmpty()){
            throw  new IllegalArgumentException("Sie müssten mindestens eine Queue mit einem step übergeben");
        }

        //Kopieren der übergebenen Queue, da die die Queue im Turn verändert wird und diese Veränderung keinen Auswirkungen auf andere Programmteile haben dürfen.
        LinkedBlockingQueue<steps> s = new LinkedBlockingQueue<steps>();
        s.addAll(steps);
        this.allowedSteps = s;

        //Wenn der Step "Verteilen" erlaubt ist werden die neuen Armeen, die zu verteilen sind erzeugt
        if(this.getAllowedSteps().contains(Turn.steps.DISTRIBUTE)  ){
            createNewArmies(this.determineAmountOfNewArmies());
        }

        //Aktuellen status auf den ersten Eintrag in der Queue setzten
        this.setCurrentStep(this.allowedSteps.poll());// Erstes element aus der Liste auf aktuellen status setzten

    }

    /**
     *
     * @return - Aktueller Spieler, der diesen Zug durchführen muss
     */
    public Player getPlayer (){
    	return this.player; 
    }

    /**
     * Gibt den Turn in einem zusammengefassten String zurück
     * @return - Zusammenfassung des Turns
     */
    public String toString(){
        return "Turn(" + this.getPlayer() + "):" + this.getCurrentStep();
    }
    
    /**
     * Berechnet die Anzahl der Armeen, die der jewilige Spieler am Anfang seines Zuges neu hinzubekommt.
     * @return Anzahl, der neuen Armeen des jeweiligen Spielers
     */
    private int determineAmountOfNewArmies(){
    	int amountNewArmies = (this.player.getCountries().size())/3;
    	amountNewArmies += this.map.getBonus(this.player);
    	if (amountNewArmies<3){
    		amountNewArmies = 3;
    	}
    	return amountNewArmies;
    }

    /**
     * Füllt die Liste der neuen Armeen
     * @param numberOfArmysToCreate - Anzahl wie viele Armeen erstellt werden sollen
     */
    private void createNewArmies(int numberOfArmysToCreate){
    	for (int i = 0; i<numberOfArmysToCreate; i++){
    		this.newArmies.add(new Army(this.player));
    	}
    }


    /**
     * Fügt der Liste der bereits verschobenen Einheiten die Armee hinzu
     * @param a bewegte Armee
     */

    private void addMovedArmy(Army a){
    	this.movedArmies.add(a);
    }
    /**
     * Prüft ob die Armee bereits verschoben wurde in diesem Zug
     * @param a Armee, die Überprüft werden soll
     * @return boolean -> true wenn die Armee bereits verschoben wurde, false, wenn sie nioch nicht verschoben wurde
     */
    private boolean isArmyAlreadyMoved(Army a){
    	return movedArmies.contains(a);
    }


    /**
     * Diese Methode dient der Überprüfung, ob der übergebene step im Moment erlaubt wäre druchführen.
     * @param stepToCheck Der Step, der überprüft werden soll
     * @return Wenn der Step erlaubt ist True, False tritt nicht auf es werden Exceptions für False ausgelöst.
     * @throws TurnNotAllowedStepException
     * @throws TurnNotInCorrectStepException
     * @throws ToManyNewArmysException
     */
    private boolean isStepAllowed(steps stepToCheck) throws ToManyNewArmysException, TurnNotAllowedStepException, TurnNotInCorrectStepException{

        if(this.getCurrentStep() == stepToCheck){
            return true;
        }
        else if(allowedSteps.contains(stepToCheck)){

            if(this.getCurrentStep() == steps.DISTRIBUTE){
                if(this.getNewArmysSize() > 0 ){
                    throw new ToManyNewArmysException(this);
                }

            }else if (this.getCurrentStep() == steps.FIGHT){


            }else if (this.getCurrentStep() == steps.MOVE){

            }
            else {
                throw new TurnNotInCorrectStepException(stepToCheck,this);
            }

        }
        else{
            throw new TurnNotAllowedStepException(stepToCheck,this);
        }
        return true;
    }


    /**
     * Per Default der erste Step, der durchgeführt wird. Diese Methode dient dazu eine Armee auf der angegebenen Position zu plazieren.
     * @see main.java.logic.Turn.steps
     * @see Turn#getDefaultSteps()
     * @param position - Das Land auf welches die neue Armee plaziert werden soll
     * @param numberOfArmys - Wieviele Einheiten auf diesem Land plaziert werden sollen.
     * @throws TurnNotAllowedStepException
     * @throws TurnNotInCorrectStepException
     * @throws NotEnoughNewArmysException
     */
    public void placeNewArmy(Country position, int numberOfArmys) throws ToManyNewArmysException, TurnNotAllowedStepException, TurnNotInCorrectStepException,NotEnoughNewArmysException,NotTheOwnerException {
        for(int i = 0; i!= numberOfArmys; i++){
            this.placeNewArmy(position);
        }
    }
    /**
     * Per Default der erste Step, der durchgeführt wird. Diese Methode dient dazu eine Armee auf der angegebenen Position zu plazieren.
     * @see main.java.logic.Turn.steps
     * @see Turn#getDefaultSteps()
     * @param position - Das Land auf welches die neue Armee plaziert werden soll
     * @throws TurnNotAllowedStepException
     * @throws TurnNotInCorrectStepException
     * @throws NotTheOwnerException
     * @throws NotEnoughNewArmysException
     */
    public void placeNewArmy(Country position) throws  ToManyNewArmysException,TurnNotAllowedStepException, TurnNotInCorrectStepException,NotEnoughNewArmysException, NotTheOwnerException{
        if (position.getOwner() != this.getPlayer())
        {
            throw  new NotTheOwnerException(this.getPlayer(), position);
        }
        if(this.isStepAllowed(steps.DISTRIBUTE)){
            //Einmal eine neue Armee plaziert ==> Statusänderung im Turn
            this.setCurrentStep(steps.DISTRIBUTE);

            if(this.newArmies.size() == 0 ){
                throw new NotEnoughNewArmysException(this);
            }
            else {
                Army a = this.newArmies.pop();
                try {
                    a.setPosition(position);
                }catch (CountriesNotConnectedException e){
                    //Nicht möglich, dass diese Exception auftritt.
                    throw new RuntimeException(e);
                }
            }

        }
    }

    /**
     * Angreifen eines Landes mit einer definierten Anzahl von einheiten
     * @param from - Von diesem Land wird angegriffen
     * @param to - Dieses land soll angegrifffen werden
     * @throws TurnNotAllowedStepException
     * @throws CountriesNotConnectedException 
     * @throws InvalidPlayerException 
     * @throws InvalidAmountOfArmiesException 
     * @throws NotEnoughArmiesToDefendException 
     * @throws NotEnoughArmiesToAttackException
     * @throws NotTheOwnerException
     * @throws ToManyNewArmysException
     */
    public Fight fight (Country from, Country to) throws TurnNotInCorrectStepException, TurnNotAllowedStepException, ToManyNewArmysException, NotTheOwnerException{

        if (from.getOwner() != this.getPlayer())
        {
            throw  new NotTheOwnerException(this.getPlayer(), from);
        }

        if(this.isStepAllowed(steps.FIGHT)){
            this.isComplete();
            //Einmal ein Land angegriffen ändert den step des Turns
            this.setCurrentStep(steps.FIGHT);
            return  new Fight(from, to, this);

        }
        throw new RuntimeException("Codeteile nicht erlaubt !");
    }


    /**
     * Bewegt eine Einheit von einem Land in ein anderes Land.
     * @param from Land von dem aus sich die Einheit bewegen soll
     * @param to Zielland
     * @param numberOfArmies Anzahl der Armeen
     * @throws NotEnoughArmysToMoveException
     * @throws TurnNotAllowedStepException
     * @throws TurnNotInCorrectStepException
     * @throws CountriesNotConnectedException
     * @throws ArmyAlreadyMovedException
     * @throws NotTheOwnerException
     */
    public void moveArmy(Country from,Country to, int numberOfArmies) throws ToManyNewArmysException, NotEnoughArmysToMoveException, TurnNotAllowedStepException, TurnNotInCorrectStepException, CountriesNotConnectedException, ArmyAlreadyMovedException,NotTheOwnerException {



        List<Army> armies = (List<Army>) from.getArmyList().clone();
        //Löschen aller Armeen, die bereits bewegt wurden, somit können nur die Armen versucht werden zu bwegen, die noch nicht bewegt wurde.
        for(Army army : armies){
            if(this.movedArmies.contains(army)){
                armies.remove(army);
            }
        }

        for(int i = 0; i!= numberOfArmies; i++){
            Army army = armies.get(armies.size()-1);
            moveArmy(from,to,army);
        }
    }
    /**
     * Bewegt eine Armee auf die neue Position.
     * Dise Methdoe bildet den 2. Step in einem Zug ab.
     *
     * @param from - Ausgangsland
     * @param to - Neues Land
     * @param army - Die Armee, die bewegt werden soll
     * @throws CountriesNotConnectedException
     * @throws ArmyAlreadyMovedException
     */
    public void moveArmy(Country from,Country to, Army army) throws ToManyNewArmysException,NotEnoughArmysToMoveException,TurnNotAllowedStepException, TurnNotInCorrectStepException, CountriesNotConnectedException, ArmyAlreadyMovedException, NotTheOwnerException {

        if (from.getOwner() != this.getPlayer())
        {
            throw  new NotTheOwnerException(this.getPlayer(), from);
        }
        else if (to.getOwner() != this.getPlayer()){
            throw  new NotTheOwnerException(this.getPlayer(), to);
        }

        if(this.isStepAllowed(steps.MOVE)){

            this.allowedSteps.clear(); // Alle steps löschen, da nach einmak move nichts anderes mehr erlaubt ist
            //Einmal eine Einheit bewegt, ändert den Step des Turns
            this.setCurrentStep(steps.MOVE);
            if(!from.isConnected(to)){
                throw new CountriesNotConnectedException(from,to);
            }
            else if (isArmyAlreadyMoved(army)){
                throw new ArmyAlreadyMovedException(army);
            }
            else if(from.getNumberOfArmys() == 1){
                throw new NotEnoughArmysToMoveException(from);
            }
            else {
                from.removeArmy(army);
                army.setPosition(to);
                addMovedArmy(army);
            }
        }
	}

    /**
     * Überprüft, ob der Turn abgeschlossen wurde.
     * @return True wenn der Turn abgeschlossen wurde, false wenn nicht
     * @throws ToManyNewArmysException
     */
    public boolean isComplete() throws ToManyNewArmysException{

        if(this.getCurrentStep() == steps.DISTRIBUTE && this.newArmies.size() > 0) {
            throw new ToManyNewArmysException(this);
        }
        return true;
    }

    /**
     * Gibt den aktuellen Step zurück
     * @return
     */
    public steps getCurrentStep() {
        return currentStep;
    }

    /**
     * Gibt den folgenden step zurück. Ändert jedoch keine Eigenschaften des Turns
     * Dient dazu rauszufinden welcher step als nächstes dran wäre. Dabei kann null zurückgegeben werden, sobald kein nächster Step mehr da ist.
     * @return - Nächster Step der dran wäre
     */
    public steps getNextStep (){
        return this.allowedSteps.peek();
    }

    /**
     * Versetzt den Turn in die nächste Stufe.
     *
     * @throws TurnCompleteException
     * @throws ToManyNewArmysException
     */
    public void setNextStep() throws TurnCompleteException, ToManyNewArmysException {
        if(this.isComplete()){
            throw new TurnCompleteException();
        }
        this.currentStep = this.allowedSteps.poll();
    }

    /**
     * Setzt den currentStep
     * @param step - Step der gesetz werden soll
     */
    private void setCurrentStep(steps step) {
        this.allowedSteps.remove(this.getCurrentStep());
        currentStep = step;
    }

    /**
     * Gibt die Anzahl der noch zu verteilenden Armeen zurück
     * @see #placeNewArmy(Country)
     * @return - Anzahl der noch zu verteilenden Armeen
     */
    public int getNewArmysSize() {
        return this.newArmies.size();
    }

    /**
     * Gibt die in diesem Turn erlaubten steps zurück.
     * @return - In diesem Turn erlaubte steps
     */
    public Queue<steps>  getAllowedSteps() {
        return  this.allowedSteps;
    }

}
