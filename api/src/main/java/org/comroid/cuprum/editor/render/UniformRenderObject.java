package org.comroid.cuprum.editor.render;

import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

public interface UniformRenderObject extends EditorComponent.Holder, ViewContainer {
    @Override
    default ITransform getTransform() {
        return new Transform.EditorToCanvasAdapter(getComponent().getTransform(), getView());
    }

    default boolean outOfView() {
        return getView().outOfView(getTransform().getPosition());
    }
}
