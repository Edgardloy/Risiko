package main.java.ui.GUI;

import javax.swing.*;

import main.java.logic.exceptions.GameNotStartedException;
import main.java.ui.CUI.utils.IO;
import main.java.logic.data.Country;
import main.java.logic.data.Map;
import main.java.logic.Turn;
import main.java.logic.Game;
import main.java.ui.GUI.country.JCountryGUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.util.HashMap;

/**
 * Created by Stefan on 09.06.14.
 */
public class JMapGUI extends JPanel {

    private Image mapImage;
    private BufferedImage mapBgImg;
    private final Map map;
    private final Game game;
    private final HashMap<Color,Country> countrys = new HashMap<Color,Country>();


    public class OnCountryClickActionListener extends MouseAdapter  {

        @Override
        public void mouseClicked(MouseEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Color col = new Color(getMapBgImg().getRGB(x, y));
            Country country = getCountry(col);
            if (country == null){
                IO.println("Country nicht gefunden");
                IO.println("R" + col.getRed() + " G" + col.getGreen() + " B" + col.getBlue());
            }
            else
            {
                Turn currentTurn;
                try {
                   currentTurn =  game.getCurrentRound().getCurrentTurn();
                }catch (GameNotStartedException e){
                    //@todo Exception Handling
                    IO.println(e.getMessage());
                    return;
                }
                JCountryGUI countryGUI= new JCountryGUI(country, currentTurn);
                countryGUI.show(event.getComponent(),x,y);

            }


        }
    }



    public JMapGUI(Game game){
        super();
        Dimension dim = new Dimension(643,180);
        this.game = game;
        this.map = game.getMap();
        for(Country country : map.getCountries()){
            this.countrys.put(country.getColor(), country);
        }
        inititize();
    }

    private void inititize (){
        Dimension dim = new Dimension(600,400);

        this.mapImage = (new ImageIcon(getClass().getResource("/resources/Map_Vg.png"))).getImage();
        this.mapBgImg = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        this.mapBgImg.getGraphics().drawImage(new ImageIcon(getClass().getResource("/resources/Map_Bg.png")).getImage(), 0, 0, dim.width, dim.height, this);
        this.addMouseListener(new OnCountryClickActionListener());

        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
        this.setMinimumSize(dim);
        this.setSize(dim);
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        super.paint(g);

        //Hintergrund Karte
        this.mapBgImg  = this.getScaledImage(this.mapBgImg, this.getWidth(), this.getHeight());

        //Paint Karte
        g.drawImage(mapImage, 0, 0, this.getWidth(), this.getHeight(), this);

    }


    private BufferedImage getScaledImage(BufferedImage image, int width, int height){
        int type=0;
        type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height,type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public BufferedImage getMapBgImg(){
        return this.mapBgImg;
    }

    public Country getCountry(Color col){
        return countrys.get(col);
    }
}
