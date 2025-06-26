package org.comroid.cuprum.editor.render;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public interface RenderComponent<T extends EditorComponent> extends UniformRenderObject {
    T getComponent();

    default Stream<Vector> getVertices() {
        return Stream.of(getPosition());
    }

    default Collection<? extends RenderComponent<?>> getChildren() {
        return Collections.emptySet();
    }

    @Override
    default boolean outOfView() {
        return getVertices().allMatch(getView()::outOfView);
    }
}
