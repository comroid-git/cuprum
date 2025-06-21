package org.comroid.cuprum.component;

import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.PositionSupplier;

import java.util.stream.Stream;

public interface ConnectionPoint extends SimulationComponent {
    @Override
    default Stream<PositionSupplier> getSnappingPoints() {
        return Stream.of(this);
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
