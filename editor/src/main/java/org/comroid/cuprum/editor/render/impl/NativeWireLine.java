package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.component.WireLine;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.editor.render.util.GraphicsUtils;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.component.WireImpl;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeWireLine extends NativeRenderObject<Wire> implements WireLine {
    public static Wire simple(ITransform basePosCom, Vector secondPosRel) {
        return new WireImpl(basePosCom, PositionSupplier.of(secondPosRel));
    }

    public NativeWireLine(Wire wire) {
        super(wire);
    }

    @Override
    public void paint(NativeEditor e, Graphics2D g) {
        if (outOfView()) return;

        var iter = getVertices().iterator();
        if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");

        var posA = iter.next();
        while (iter.hasNext()) {
            var posB = iter.next();

            GraphicsUtils.prepareConducitveGraphics(component, g);
            g.drawLine((int) posA.getX(), (int) posA.getY(), (int) posB.getX(), (int) posB.getY());

            posA = posB;
        }
    }
}
