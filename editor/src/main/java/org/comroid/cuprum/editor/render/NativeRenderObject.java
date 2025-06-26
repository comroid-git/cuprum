package org.comroid.cuprum.editor.render;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.View;
import org.comroid.cuprum.editor.render.impl.NativeContactorSymbol;
import org.comroid.cuprum.editor.render.impl.NativeSolderPointCircle;
import org.comroid.cuprum.editor.render.impl.NativeWireLine;

import java.awt.*;

@Value
@NonFinal
public abstract class NativeRenderObject<T extends EditorComponent> implements RenderComponent<T> {
    protected T component;

    @Override
    public View getView() {
        return NativeEditor.INSTANCE.getView();
    }

    public void paint(NativeEditor e, Graphics2D g) {
        getChildren().stream().flatMap(Streams.cast(NativeRenderObject.class)).forEach(child -> child.paint(e, g));
    }

    @Value
    public static class Adapter implements RenderObjectAdapter {
        @Override
        public NativeSolderPointCircle createSolderPoint(ConnectionPoint connectionPoint) {
            return new NativeSolderPointCircle(connectionPoint);
        }

        @Override
        public NativeWireLine createWireLine(Wire wire) {
            return new NativeWireLine(wire);
        }

        @Override
        public UniformRenderObject createContactor(Contactor contactor) {
            return new NativeContactorSymbol(contactor);
        }
    }
}
