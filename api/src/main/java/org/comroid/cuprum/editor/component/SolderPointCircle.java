package org.comroid.cuprum.editor.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.editor.render.RenderComponent;

import java.util.stream.Stream;

public interface SolderPointCircle extends RenderComponent<ConnectionPoint> {
    @Override
    default Stream<Vector> getVertices() {
        return Stream.of(getPosition());
    }
}
