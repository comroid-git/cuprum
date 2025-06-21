package org.comroid.cuprum.editor.model;

import javax.swing.*;

public class CPanel extends JPanel {
    @Override
    public void setVisible(boolean state) {
        super.setVisible(state);
        for (var component : getComponents()) component.setVisible(state);
    }
}
