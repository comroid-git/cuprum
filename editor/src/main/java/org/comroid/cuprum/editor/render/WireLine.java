package org.comroid.cuprum.editor.render;

import lombok.Value;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;

import java.awt.*;

@Value
public class WireLine implements AwtRenderObject {
    Wire wire;

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        var view = e.getView();
        if (view.outOfView(wire.getPositionA()) || view.outOfView(wire.getPositionB())) return;
        var posA = view.transformToView(wire.getPositionA());
        var posB = view.transformToView(wire.getPositionB());

        g.setStroke(new BasicStroke(1.5f));
        g.setColor(Color.BLUE);
        g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());
    }
}
