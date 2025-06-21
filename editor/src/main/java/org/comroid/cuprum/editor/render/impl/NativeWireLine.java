package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.component.WireLine;
import org.comroid.cuprum.editor.render.NativeRenderObject;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeWireLine extends NativeRenderObject<Wire> implements WireLine {
    public NativeWireLine(Wire component) {
        super(component);
    }

    @Override
    public void paint(NativeEditor e, Graphics2D g) {
        if (outOfView()) return;

        var iter = getVertices().iterator();
        if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");

        var posA = iter.next();
        while (iter.hasNext()) {
            var posB = iter.next();

            g.setStroke(new BasicStroke((float) component.getCrossSection()));
            g.setColor(component.getMaterial().color);
            g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());

            posA = posB;
        }
    }
}
