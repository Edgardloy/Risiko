package main.java.ui.GUI.country;

//import com.sun.codemodel.internal.JMod;
import main.java.logic.Fight;
import main.java.logic.Turn;
import main.java.logic.data.Country;
import main.java.ui.GUI.utils.JModalDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**star
 * Created by Stefan on 18.06.14.
 */
public class JFightGUI extends JModalDialog {
    private final Fight fight;
    private class CloseBtnListener implements ActionListener {

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Window root = JFightGUI.this.getOwner();
            if (root != null ){
                root.repaint(); // Repaint der gesamten Karte beim Schließen des Fights
            }
            JFightGUI.this.dispose();
        }
    }
    public JFightGUI(final Component parent, final Fight fight)  {
        super(parent,"Fight",ModalityType.APPLICATION_MODAL);
        this.fight = fight;
        this.setLayout(new BorderLayout(5,5));

        JPanel centerPanel = new JPanel();

        centerPanel.add(new JFightSide(this.fight, JFightSide.sides.DEFENDER));
        centerPanel.add(new JFightSide(this.fight,JFightSide.sides.AGGRESSOR));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(centerPanel,BorderLayout.CENTER);

        //Close btn
        JButton closeBtn = new JButton("Kampf verlassen");
        closeBtn.addActionListener(new CloseBtnListener());
        this.add(closeBtn,BorderLayout.SOUTH);

        this.pack();
        this.setVisible(true);

    }


}
