package org.comroid.cuprum.component.model;

import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

public interface SimComponent extends Transform.Holder {
    default @Nullable UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return null;
    }

    interface Holder extends Transform.Holder {
        SimComponent getComponent();

        @Override
        default ITransform getTransform() {
            return getComponent().getTransform();
        }
    }
}
