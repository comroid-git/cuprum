package org.comroid.cuprum.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;

import java.util.stream.Stream;

public interface ConnectionPoint extends SimulationComponent {
    @Override
    default Stream<Vector.N2> getSnappingPoints() {
        return Stream.of(getPosition());
    }

    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createSolderPoint(this);
    }

    @Override
    default int priorityLayer() {
        return 100;
    }
}
