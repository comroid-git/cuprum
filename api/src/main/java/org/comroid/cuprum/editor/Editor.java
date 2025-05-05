package org.comroid.cuprum.editor;

import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;

public interface Editor extends ViewContainer {
    RenderObjectAdapter getRenderObjectAdapter();
}
