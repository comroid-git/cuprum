package org.comroid.cuprum.editor.render;

import org.comroid.cuprum.component.model.Component;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.spatial.Transform;

public interface UniformRenderObject extends Component.Holder, ViewContainer {
    @Override
    default Transform getTransform() {
        return Component.Holder.super.getTransform().new CanvasToViewAdapter(getView());
    }

    default boolean outOfView() {
        return getView().outOfView(Component.Holder.super.getTransform().getPosition());
    }
}
