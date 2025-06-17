package org.comroid.cuprum.editor.model;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.delegate.EditorDelegate;

public record SnappingPointCandidate(EditorComponent component, Vector.N2 position) {
    public SnappingPoint renderObject() {
        return EditorDelegate.INSTANCE.createSnappingPoint(component, position);
    }
}
