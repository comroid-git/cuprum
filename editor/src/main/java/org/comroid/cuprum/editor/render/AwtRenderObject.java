package org.comroid.cuprum.editor.render;

import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.RenderObjectAdapter;
import org.comroid.cuprum.editor.UniformRenderObject;

import java.awt.*;

public interface AwtRenderObject extends UniformRenderObject {
    void paint(AwtEditor e, Graphics2D g);

    @Value
    class Adapter implements RenderObjectAdapter {
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
