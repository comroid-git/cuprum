package org.comroid.cuprum.editor;

import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;

import java.util.Arrays;
import java.util.stream.Stream;

public interface Editor extends ViewContainer {
    Stream<? extends SimComponent> getSimComponents();

    RenderObjectAdapter getRenderObjectAdapter();

    default void add(SimComponent... components) {
        Arrays.stream(components).forEach(this::add);
    }

    void add(SimComponent component);

    default long remove(SimComponent... components) {
        return Arrays.stream(components).filter(this::remove).count();
    }

    boolean remove(SimComponent component);
}
