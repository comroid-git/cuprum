package org.comroid.cuprum.editor.render.util;

import org.comroid.cuprum.component.model.basic.Conductive;

import java.awt.*;

public final class GraphicsUtils {
    public static void prepareConducitveGraphics(Conductive component, Graphics2D g) {
        var crossSection = component.getCrossSection();
        g.setStroke(new BasicStroke(Double.isNaN(crossSection) ? 1.5f : (float) crossSection));

        g.setColor(component.getMaterial().color);
    }

    private GraphicsUtils() {
        throw new UnsupportedOperationException("nope");
    }
}
