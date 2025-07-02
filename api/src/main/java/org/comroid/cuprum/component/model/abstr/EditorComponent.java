package org.comroid.cuprum.component.model.abstr;

import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

public interface EditorComponent extends CuprumComponent, SnappableComponent, Transform.Holder {
    UniformRenderObject createRenderObject(RenderObjectAdapter adapter);

    default int priorityLayer() {
        return 0;
    }

    interface Holder extends Transform.Holder {
        EditorComponent getComponent();

        @Override
        default ITransform getTransform() {
            return getComponent().getTransform();
        }
    }
}
