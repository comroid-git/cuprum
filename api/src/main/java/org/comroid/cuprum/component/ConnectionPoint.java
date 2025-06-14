package org.comroid.cuprum.component;

import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;

public interface ConnectionPoint extends SimComponent {
    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createSolderPointCircle(this);
    }
}
