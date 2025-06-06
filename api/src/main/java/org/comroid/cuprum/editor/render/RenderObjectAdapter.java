package org.comroid.cuprum.editor.render;

import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;

public interface RenderObjectAdapter {
    UniformRenderObject createSolderPointCircle(ConnectionPoint connectionPoint);
    UniformRenderObject createWireLine(Wire wire);
}
