package org.comroid.cuprum.delegate;

import org.comroid.annotations.Instance;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.model.SnappingPoint;

import java.util.ServiceLoader;

public interface EditorDelegate {
    @Instance EditorDelegate INSTANCE = ServiceLoader.load(EditorDelegate.class).findFirst().orElseThrow();

    SnappingPoint createSnappingPoint(EditorComponent component, Vector.N2 position);
}
