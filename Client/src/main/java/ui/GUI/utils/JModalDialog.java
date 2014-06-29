package main.java.ui.GUI.utils;

import main.java.logic.exceptions.UserCanceledException;

import javax.swing.*;
import java.awt.*;

/**
 *
 * Klasse, die zur Darstellung eines modalen Dialogs gedacht ist
 *
 * @see javax.swing.JDialog
 */
public class JModalDialog extends JDialog {

    /**
     *
     * Fragt einen Nutzer nach einem Int Wert
     * Dabei können Maximale und Minimale Werte vorgegbene werden und der Nutzer muss eine gültige Angabe zurück liefern
     * @param com Kontext in dem das modale dialog angezeigt werden soll
     *            Sozusagen das Parent Window
     * @param title Titel für das Fenster
     * @param message Nachricht, die dem Nutzer angezeigt werden soll
     * @param min Minimaler Wert der als Eingabe erlaubt ist
     * @param max Maximaler Wert, der als Eingabe erlaubt ist
     * @return Eingabe des Benutzers
     * @throws UserCanceledException Wenn der Nutzer die Aktion abbricht
     */
    public static int showAskIntegerModal(final Component com, final String title, String message, int min, final int max) throws UserCanceledException {

        Window frame =  SwingUtilities.getWindowAncestor(com);
        if (min > max) {
            min = max;
        }
        boolean validInput = false;
        int number = 0;

        //Solange eingabe verlange bis korrekt oder abgebrochen
        do {
            String result = JOptionPane.showInputDialog(frame, message, title);
            try {
                number = Integer.parseInt(result);
                if (number >= min && number <= max) {
                    validInput = true;
                } else {
                    message = result + " ist keine gültige Anzahl, bitte geben Sie eine gültige Anzahl ein, die zwischen " + min + " und " + max + " liegt.";
                }
            } catch (NumberFormatException e) {
                validInput = false;
                message = result + " ist keine gültige Zahl, Bitte geben Sie eine Zahl zwischen " + min + " und " + max + " ein.";
                //Benutzer Klick auf Abbrechen
                if (result == null) {
                    break;
                }
            }

        } while (!validInput);

        if (validInput == false) {
            throw new UserCanceledException();
        }
        return number;

    }


    public static void showInfoDialog(final Component com, final String title, final String message) {
        Window frame =  SwingUtilities.getWindowAncestor(com);
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static String showInputDialog(Window frame, String message, String title) {
        return JOptionPane.showInputDialog(frame, message, title);
    }

    public JModalDialog(Component panel, String title, ModalityType type) {
        super(SwingUtilities.getWindowAncestor(panel), title, type);
        Dimension dim = new Dimension(300, 500);
        this.setMinimumSize(dim);
        this.setPreferredSize(dim);
        this.centerModal(panel);

    }
    public void centerModal (Component parent){
        Window root = SwingUtilities.getWindowAncestor(parent);
        Dimension dim = this.getSize();
        if(root != null){
            //Center positions
            int width = (int) (root.getWidth()/2-dim.getWidth()/2);
            int height = (int) (root.getHeight()/2-dim.getHeight()/2);
            this.setLocation(width,height);
        }
    }
}