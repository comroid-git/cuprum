package org.comroid.cuprum.editor.render;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class WireLine extends AwtRenderObject {
    Wire component;

    @Override
    public boolean outOfView() {
        var view = getView();
        return view.outOfView(component.getPositionA()) && view.outOfView(component.getPositionB());
    }

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        if (outOfView()) return;

        Vector.N2 posA = component.getPositionA(), posB = component.getPositionB();
        g.setStroke(new BasicStroke((float) component.getCrossSection()));
        g.setColor(component.getMaterial().color);
        g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());
    }
}
