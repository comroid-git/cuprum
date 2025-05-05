package org.comroid.cuprum.editor;

import org.comroid.cuprum.editor.render.RenderObjectAdapter;

public interface Editor {
    RenderObjectAdapter getRenderObjectAdapter();

    View getView();
}
