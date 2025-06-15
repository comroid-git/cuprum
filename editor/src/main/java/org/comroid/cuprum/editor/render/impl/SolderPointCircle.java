package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.render.AwtRenderObject;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class SolderPointCircle extends AwtRenderObject {
    public static final int DIAMETER = 8;
    ConnectionPoint component;

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        if (outOfView()) return;

        var pos    = getPosition();
        var radius = DIAMETER / 2;
        g.setColor(Color.BLACK);
        g.fillOval((int) (pos.getX() - radius), (int) (pos.getY() - radius), DIAMETER, DIAMETER);
    }
}
