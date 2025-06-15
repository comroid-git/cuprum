package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.render.AwtRenderObject;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class WireLine extends AwtRenderObject {
    Wire component;

    @Override
    public boolean outOfView() {
        return component.getSegments().stream().map(Wire.Segment::position).map(getView()::transformEditorToCanvas).allMatch(getView()::outOfView);
    }

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        if (outOfView()) return;

        var iter = component.getSegments().iterator();
        if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");
        var last   = iter.next();
        var offset = getTransform().getPosition();
        while (iter.hasNext()) {
            var    it   = iter.next();
            Vector posA = last.position().addi(offset), posB = it.position().addi(offset);

            g.setStroke(new BasicStroke((float) component.getCrossSection()));
            g.setColor(component.getMaterial().color);
            g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());

            last = it;
        }
    }
}
