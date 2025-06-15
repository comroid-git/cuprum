package org.comroid.cuprum.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;

import java.util.stream.Stream;

public interface ConnectionPoint extends SimComponent {
    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createSolderPoint(this);
    }

    @Override
    default Stream<Vector.N2> getSnappingPoints() {
        return Stream.of(getPosition());
    }
}
