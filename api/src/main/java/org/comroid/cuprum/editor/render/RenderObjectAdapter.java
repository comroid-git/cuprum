package org.comroid.cuprum.editor.render;

import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.component.Wire;

public interface RenderObjectAdapter {
    UniformRenderObject createSolderPoint(ConnectionPoint connectionPoint);

    UniformRenderObject createWireLine(Wire wire);

    UniformRenderObject createContactor(Contactor contactor);
}
