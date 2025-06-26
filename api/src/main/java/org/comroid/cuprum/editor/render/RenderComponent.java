package org.comroid.cuprum.editor.render;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;

import java.util.stream.Stream;

public interface RenderComponent<T extends EditorComponent> extends UniformRenderObject {
    T getComponent();

    default Stream<Vector> getVertices() {
        return Stream.of(getPosition());
    }

    @Override
    default boolean outOfView() {
        return getVertices().allMatch(getView()::outOfView);
    }
}
