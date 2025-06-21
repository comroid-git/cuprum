package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.component.WireLine;
import org.comroid.cuprum.editor.render.AwtRenderObject;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class AwtWireLine extends AwtRenderObject<Wire> implements WireLine {
    public AwtWireLine(Wire component) {
        super(component);
    }

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        if (outOfView()) return;

        var iter = getVertices().iterator();
        if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");
        var last   = iter.next();
        var offset = getTransform().getPosition();
        while (iter.hasNext()) {
            var    it   = iter.next();
            Vector posA = last.addi(offset), posB = it.addi(offset);

            g.setStroke(new BasicStroke((float) component.getCrossSection()));
            g.setColor(component.getMaterial().color);
            g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());

            last = it;
        }
    }
}
