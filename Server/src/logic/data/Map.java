package logic.data;


import interfaces.data.IContinent;
import interfaces.data.ICountry;
import interfaces.data.IMap;
import interfaces.data.IPlayer;

import java.awt.*;
import java.util.*;

/**
 * @author Jennifer Theloy, Thu Nguyen, Stefan Bieliauskas
 *
 * Dient zur Verwaltung der Karte für ein Spiel
 */
public class Map  implements IMap{


    public static final String DEFAULT_MAP_UUID = "03c64c20-fc74-11e3-a3ac-0800200c9a66";
    /**
     * Beinhaltet alle Länder, für die Karte
     */
    private final ArrayList<ICountry> countries = new ArrayList<ICountry>();

    /**
     * Beinhaltet alle Kontinente für die Karte
     */
    private final ArrayList<IContinent> continents = new ArrayList<IContinent>();

    /**
     * ID der Karte
     */
    private final UUID id = UUID.fromString(Map.DEFAULT_MAP_UUID);
    /**
     * Erstellt eine neue Standard-Karte
     */
    public Map() {

        //Kontinente erzeugen
        Continent northAmerica  = new Continent("Nord Amerika", 5);
        Continent southAmerica  = new Continent("Süd Amerika", 2);
        Continent europe        = new Continent("Europa", 5);
        Continent asia          = new Continent("Asien", 7);
        Continent afrika        = new Continent("Afrika", 3);
        Continent australia     = new Continent("Australien", 2);

        this.continents.add(northAmerica);
        this.continents.add(southAmerica);
        this.continents.add(asia);
        this.continents.add(afrika);
        this.continents.add(australia);


        //Länder zuweisen
        this.countries.add(new Country("Alaska", northAmerica,                  new Color(222,55,0)));
        this.countries.add(new Country("Nordwest-Territorium", northAmerica,    new Color(255,222,111)));
        this.countries.add(new Country("Alberta", northAmerica,                 new Color(150,0,222)));
        this.countries.add(new Country("Ontario", northAmerica,                 new Color(0,222,150)));
        this.countries.add(new Country("Quebec", northAmerica,                  new Color(222,111,150)));
        this.countries.add(new Country("Weststaaten", northAmerica,             new Color(222,0,222)));
        this.countries.add(new Country("Oststaaten", northAmerica,              new Color(222,111,222)));
        this.countries.add(new Country("Mittelamerika", northAmerica,           new Color(222,111,111)));
        this.countries.add(new Country("Hawaii", northAmerica,                  new Color(255,222,22)));
        this.countries.add(new Country("Nunavut", northAmerica,                 new Color(222,40,111)));
        
        this.countries.add(new Country("Venezuela", southAmerica,               new Color(111,111,222)));
        this.countries.add(new Country("Brasilien", southAmerica,               new Color(50,150,50)));
        this.countries.add(new Country("Peru", southAmerica,                    new Color(222,0,111)));
        this.countries.add(new Country("Falkland-Inseln", southAmerica,         new Color(50,150,150)));
        this.countries.add(new Country("Argentinien", southAmerica,             new Color(50,150,100)));
        
        this.countries.add(new Country("Grönland", europe,                      new Color(0,255,0)));
        this.countries.add(new Country("Island", europe,                        new Color(22,255,123)));
        this.countries.add(new Country("Großbritannien", europe,                new Color(22,111,222)));
        this.countries.add(new Country("Skandinavien", europe,                  new Color(50,120,120)));
        this.countries.add(new Country("Mitteleuropa", europe,                  new Color(255,111,137)));
        this.countries.add(new Country("Westeuropa", europe,                    new Color(0,255,199)));
        this.countries.add(new Country("Südeuropa", europe,                     new Color(30,99,255)));
        this.countries.add(new Country("Ukraine", europe,                       new Color(255,255,0)));
        this.countries.add(new Country("Svalbard", europe,                      new Color(50,60,120)));
        
        this.countries.add(new Country("Afghanistan", asia,                     new Color(255,200,0)));
        this.countries.add(new Country("Ural", asia,                            new Color(105,8,90)));
        this.countries.add(new Country("Sibirien", asia,                        new Color(100,0,0)));
        this.countries.add(new Country("Irrutsk", asia,                         new Color(0,0,100)));
        this.countries.add(new Country("Jakutsk", asia,                         new Color(0,100,0)));
        this.countries.add(new Country("Kamtschatka", asia,                     new Color(0,158,225)));
        this.countries.add(new Country("Mongolei", asia,                        new Color(234,105,160)));
        this.countries.add(new Country("Japan", asia,                           new Color(227,0,122)));
        this.countries.add(new Country("China", asia,                           new Color(255,0,0)));
        this.countries.add(new Country("Indien", asia,                          new Color(171,140,188)));
        this.countries.add(new Country("Siam", asia,                            new Color(151,191,13)));
        this.countries.add(new Country("Mittlerer-Osten", asia,                 new Color(0,255,255)));
        
        this.countries.add(new Country("Ägypten", afrika,                       new Color(200,100,100)));
        this.countries.add(new Country("Ostafrika", afrika,                     new Color(100,200,100)));
        this.countries.add(new Country("Nordwestafrika", afrika,                new Color(50,150,200)));
        this.countries.add(new Country("Südafrika", afrika,                     new Color(0,200,200)));
        this.countries.add(new Country("Madagaskar", afrika,                    new Color(0,200,0)));
        this.countries.add(new Country("Kongo", afrika,                         new Color(50,100,200)));
        
        this.countries.add(new Country("Indonesien", australia,                 new Color(122,0,122)));
        this.countries.add(new Country("Neu-Guinea", australia,                 new Color(100,0,200)));
        this.countries.add(new Country("Ostaustralien", australia,              new Color(200,100,0)));
        this.countries.add(new Country("Westaustralien", australia,             new Color(255,255,100)));
        this.countries.add(new Country("Neuseeland", australia,                 new Color(200,0,100)));
        this.countries.add(new Country("Philippinen", australia,                new Color(255,147,0)));



        //Hinzufügen der Verbindungen

        
        //Australien
        ICountry ostaustralien  = this.getCountry("Ostaustralien");
        ostaustralien.connectTo(this.getCountry("Westaustralien"));
        ostaustralien.connectTo(this.getCountry("Neu-Guinea"));

        ICountry indonesien  = this.getCountry("Indonesien");
        indonesien.connectTo(this.getCountry("Siam"));
        indonesien.connectTo(this.getCountry("Westaustralien"));
        indonesien.connectTo(this.getCountry("Philippinen"));
        
        ICountry Neuguinea = this.getCountry("Neu-Guinea");
        Neuguinea.connectTo(this.getCountry("Westaustralien"));
        
        ICountry neuseeland = this.getCountry("Neuseeland");
        neuseeland.connectTo(this.getCountry("Ostaustralien"));


        //Asien
        ICountry China  = this.getCountry("China");
        China.connectTo(this.getCountry("Siam"));
        China.connectTo(this.getCountry("Indien"));
        China.connectTo(this.getCountry("Mongolei"));
        China.connectTo(this.getCountry("Afghanistan"));
        China.connectTo(this.getCountry("Sibirien"));
        China.connectTo(this.getCountry("Ural"));

        ICountry mongolei  = this.getCountry("Mongolei");
        mongolei.connectTo(this.getCountry("Japan"));
        mongolei.connectTo(this.getCountry("Irrutsk"));
        mongolei.connectTo(this.getCountry("Sibirien"));

        ICountry jakutsk  = this.getCountry("Jakutsk");
        jakutsk.connectTo(this.getCountry("Kamtschatka"));
        jakutsk.connectTo(this.getCountry("Sibirien"));
        jakutsk.connectTo(this.getCountry("Irrutsk"));
        
        ICountry japan = this.getCountry("Japan");
        japan.connectTo(this.getCountry("Hawaii"));
        japan.connectTo(this.getCountry("Philippinen"));
        japan.connectTo(this.getCountry("Kamtschatka"));
        
        ICountry irrutsk = this.getCountry("Irrutsk");
        irrutsk.connectTo(this.getCountry("Kamtschatka"));
        irrutsk.connectTo(this.getCountry("Sibirien"));
        
        ICountry Indien = this.getCountry("Indien");
        Indien.connectTo(this.getCountry("Siam"));
        Indien.connectTo(this.getCountry("Mittlerer-Osten"));
        Indien.connectTo(this.getCountry("Afghanistan"));
        
        ICountry ural = this.getCountry("Ural");
        ural.connectTo(this.getCountry("Sibirien"));
        ural.connectTo(this.getCountry("Afghanistan"));
        ural.connectTo(this.getCountry("Ukraine"));
        
        ICountry mittlererOsten = this.getCountry("Mittlerer-Osten");
        mittlererOsten.connectTo(this.getCountry("Afghanistan"));
        mittlererOsten.connectTo(this.getCountry("Ukraine"));
        mittlererOsten.connectTo(this.getCountry("Ägypten"));
        mittlererOsten.connectTo(this.getCountry("Südeuropa"));
        
        
        //Europa
        ICountry ukraine = this.getCountry("Ukraine");
        ukraine.connectTo(this.getCountry("Afghanistan"));
        ukraine.connectTo(this.getCountry("Südeuropa"));
        ukraine.connectTo(this.getCountry("Skandinavien"));
        ukraine.connectTo(this.getCountry("Mitteleuropa"));
        
        ICountry Skandinavien = this.getCountry("Skandinavien");
        Skandinavien.connectTo(this.getCountry("Svalbard"));
        Skandinavien.connectTo(this.getCountry("Island"));
        Skandinavien.connectTo(this.getCountry("Großbritannien"));
        Skandinavien.connectTo(this.getCountry("Mitteleuropa"));
        
        ICountry gb = this.getCountry("Großbritannien");
        gb.connectTo(this.getCountry("Island"));
        gb.connectTo(this.getCountry("Mitteleuropa"));
        gb.connectTo(this.getCountry("Westeuropa"));
        
        ICountry westEu = this.getCountry("Westeuropa");
        westEu.connectTo(this.getCountry("Nordwestafrika"));
        westEu.connectTo(this.getCountry("Südeuropa"));
        westEu.connectTo(this.getCountry("Mitteleuropa"));
        
        ICountry southEu = this.getCountry("Südeuropa");
        southEu.connectTo(this.getCountry("Ägypten"));
        southEu.connectTo(this.getCountry("Nordwestafrika"));
        
        //Afrika
        ICountry noWeAfrika = this.getCountry("Nordwestafrika");
        noWeAfrika.connectTo(this.getCountry("Ägypten"));
        noWeAfrika.connectTo(this.getCountry("Ostafrika"));
        noWeAfrika.connectTo(this.getCountry("Kongo"));
        
        ICountry ostAfrika = this.getCountry("Ostafrika");
        ostAfrika.connectTo(this.getCountry("Ägypten"));
        ostAfrika.connectTo(this.getCountry("Kongo"));
        ostAfrika.connectTo(this.getCountry("Südafrika"));
        ostAfrika.connectTo(this.getCountry("Madagaskar"));
        
        ICountry southAfrika = this.getCountry("Südafrika");
        southAfrika.connectTo(this.getCountry("Madagaskar"));
        southAfrika.connectTo(this.getCountry("Kongo"));
        southAfrika.connectTo(this.getCountry("Falkland-Inseln"));
        

        
        //Süd-Amerika
        ICountry brasil = this.getCountry("Brasilien");
        brasil.connectTo(this.getCountry("Nordwestafrika"));
        brasil.connectTo(this.getCountry("Venezuela"));
        brasil.connectTo(this.getCountry("Peru"));
        brasil.connectTo(this.getCountry("Argentinien"));
        
        ICountry venezuela = this.getCountry("Venezuela");
        venezuela.connectTo(this.getCountry("Peru"));
        venezuela.connectTo(this.getCountry("Mittelamerika"));
        
        ICountry Argentinien = this.getCountry("Argentinien");
        Argentinien.connectTo(this.getCountry("Peru"));
        Argentinien.connectTo(this.getCountry("Falkland-Inseln"));
        Argentinien.connectTo(this.getCountry("Neuseeland"));
        
        
        //Nord-Amerika
        ICountry mittelamerika = this.getCountry("Mittelamerika");
        mittelamerika.connectTo(this.getCountry("Oststaaten"));
        mittelamerika.connectTo(this.getCountry("Weststaaten"));

        ICountry Weststaaten = this.getCountry("Weststaaten");
        Weststaaten.connectTo(this.getCountry("Oststaaten"));
        Weststaaten.connectTo(this.getCountry("Hawaii"));
        Weststaaten.connectTo(this.getCountry("Alberta"));
        Weststaaten.connectTo(this.getCountry("Ontario"));
        
        ICountry Ontario = this.getCountry("Ontario");
        Ontario.connectTo(this.getCountry("Quebec"));
        Weststaaten.connectTo(this.getCountry("Oststaaten"));
        Weststaaten.connectTo(this.getCountry("Nunavut"));
        Weststaaten.connectTo(this.getCountry("Nordwest-Territorium"));
        Weststaaten.connectTo(this.getCountry("Alberta"));

        ICountry alaska = this.getCountry("Alaska");
        alaska.connectTo(this.getCountry("Kamtschatka"));
        alaska.connectTo(this.getCountry("Nordwest-Territorium"));
        alaska.connectTo(this.getCountry("Alberta"));
        
        ICountry Nordwest = this.getCountry("Nordwest-Territorium");
        Nordwest.connectTo(this.getCountry("Alberta"));
        Nordwest.connectTo(this.getCountry("Nunavut"));
        
        ICountry Nunavut = this.getCountry("Nunavut");
        Nunavut.connectTo(this.getCountry("Quebec"));
        Nunavut.connectTo(this.getCountry("Grönland"));
        
        ICountry groenland = this.getCountry("Grönland");
        groenland.connectTo(this.getCountry("Quebec"));
        groenland.connectTo(this.getCountry("Svalbard"));
        groenland.connectTo(this.getCountry("Island"));

    }


    /**
     * Gibt die Liste aller Countries zurück
     * @return Liste aller Counties
     */
    public ArrayList<ICountry> getCountries() {
        return this.countries;
    }

    /**
     * Berechnet den Benous, den ein Spieler an Einheiten bekommt f�r die komplette Einnahme des jeweilligen Kontinents
     * @param p der aktuelle Spieler
     * @return die Anzahl der Bonus Einheiten
     */
    public int getBonus(IPlayer p){
    	int bonus = 0;
    	for (IContinent c : this.continents){
    		if(c.getCurrentOwner()==p){bonus += c.getBonus();}
    	}
    	return bonus;
    }
    
    /**
     * Vergleicht die Namen der L�nder mit �bergebenem String
     * @param n String (name des zu suchenden Landes)
     * @return das zu suchende Land
     */
    public ICountry getCountry(String n){
    	for (ICountry c : countries){
    		if(c.getName().equals(n)){
    			return c;
    		}
    	}
    	return null;
    }
    /**
     * Vergleicht die Farbe mit der übergebenen Farbe
     * @param col Color (Farbe des zu suchenden Landes)
     * @return das zu suchende Land
     */
    public ICountry getCountry(Color col){
        for (ICountry c : countries){
            if(c.getColor().equals(col)){
                return c;
            }
        }
        return null;
    }

    /**
     *
     * @return Alle Kontinente dieser Karte
     */
    public ArrayList<IContinent> getContinents() {
        return continents;
    }

    /**
     * Getter für die ID
     */
    public UUID getId(){
        return id;
    }
}

