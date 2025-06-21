package org.comroid.cuprum.editor.render;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.View;
import org.comroid.cuprum.editor.render.impl.AwtSolderPointCircle;
import org.comroid.cuprum.editor.render.impl.AwtWireLine;

import java.awt.*;

@Value
@NonFinal
public abstract class AwtRenderObject<T extends EditorComponent> implements RenderComponent<T> {
    protected T component;

    @Override
    public View getView() {
        return AwtEditor.INSTANCE.getView();
    }

    public abstract void paint(AwtEditor e, Graphics2D g);

    @Value
    public static class Adapter implements RenderObjectAdapter {
        @Override
        public AwtSolderPointCircle createSolderPoint(ConnectionPoint connectionPoint) {
            return new AwtSolderPointCircle(connectionPoint);
        }

        @Override
        public AwtWireLine createWireLine(Wire wire) {
            return new AwtWireLine(wire);
        }
    }
}
