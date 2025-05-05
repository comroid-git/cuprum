package org.comroid.cuprum.editor.render;

import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.View;

import java.awt.*;

public abstract class AwtRenderObject implements UniformRenderObject {
    @Override
    public View getView() {
        return AwtEditor.INSTANCE.getView();
    }

    public abstract void paint(AwtEditor e, Graphics2D g);

    @Value
    public static class Adapter implements RenderObjectAdapter {
        @Override
        public SolderPointCircle createSolderPointCircle(ConnectionPoint connectionPoint) {
            return new SolderPointCircle(connectionPoint);
        }

        @Override
        public WireLine createWireLine(Wire wire) {
            return new WireLine(wire);
        }
    }
}
