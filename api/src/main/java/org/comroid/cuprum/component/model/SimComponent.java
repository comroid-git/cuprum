package org.comroid.cuprum.component.model;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface SimComponent extends Transform.Holder {
    Stream<Vector.N2> getSnappingPoints();

    default @Nullable UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return null;
    }

    default int priorityLayer() {
        return 0;
    }

    interface Holder extends Transform.Holder {
        SimComponent getComponent();

        @Override
        default ITransform getTransform() {
            return getComponent().getTransform();
        }
    }
}
